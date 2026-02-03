package com.jeong.runninggoaltracker.feature.ai_coach.processing

import androidx.camera.core.ImageAnalysis
import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.SquatFrameMetrics
import com.jeong.runninggoaltracker.domain.usecase.ProcessPoseUseCase
import com.jeong.runninggoaltracker.feature.ai_coach.contract.SmartWorkoutLogContract
import com.jeong.runninggoaltracker.feature.ai_coach.data.pose.PoseDetector
import com.jeong.runninggoaltracker.feature.ai_coach.logging.WorkoutAnalyticsLogger
import com.jeong.runninggoaltracker.feature.ai_coach.presentation.DebugOverlayMode
import com.jeong.runninggoaltracker.feature.ai_coach.presentation.FeedbackStringMapper
import com.jeong.runninggoaltracker.feature.ai_coach.presentation.LungeGoodFormReason
import com.jeong.runninggoaltracker.feature.ai_coach.presentation.LungeRepSnapshot
import com.jeong.runninggoaltracker.feature.ai_coach.presentation.SmartWorkoutUiState
import com.jeong.runninggoaltracker.feature.ai_coach.speech.SpeechCoordinator
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class PoseFrameProcessor @Inject constructor(
    private val poseDetector: PoseDetector,
    private val processPoseUseCase: ProcessPoseUseCase,
    private val speechCoordinator: SpeechCoordinator,
    private val analyticsLogger: WorkoutAnalyticsLogger
) {
    private var lastAttemptActive: Boolean? = null
    private var lastDepthReached: Boolean? = null
    private var lastFullBodyVisible: Boolean? = null

    val imageAnalyzer: ImageAnalysis.Analyzer
        get() = poseDetector.imageAnalyzer

    fun clear() {
        poseDetector.clear()
    }

    fun updateSpeechCooldown(cooldownMs: Long) {
        speechCoordinator.updateCooldown(cooldownMs)
    }

    fun resetSpeechState() {
        speechCoordinator.reset()
    }

    fun results(stateFlow: StateFlow<SmartWorkoutUiState>): Flow<PoseFrameProcessingResult> =
        poseDetector.poseFrames.map { frame ->
            val currentState = stateFlow.value
            val analysis = processPoseUseCase.analyze(frame, currentState.exerciseType)
            if (analysis.skippedLowConfidence) {
                analyticsLogger.logSkippedFrame(frame.timestampMs)
            }
            if (analysis.repCount.isIncremented) {
                analyticsLogger.logRepCountUpdate(
                    source = SmartWorkoutLogContract.SOURCE_ANALYZER,
                    repCount = analysis.repCount.value,
                    timestampMs = frame.timestampMs
                )
            }
            val exerciseType = currentState.exerciseType
            val feedbackEventKey = analysis.feedbackEventKey
            val speechResult = analysis.feedbackEvent?.let { feedbackType ->
                speechCoordinator.handleFeedback(
                    feedbackType = feedbackType,
                    feedbackEventKey = feedbackEventKey,
                    exerciseType = exerciseType,
                    timestampMs = frame.timestampMs
                )
            }
            if (speechResult != null && feedbackEventKey != null) {
                analysis.feedbackEvent?.let { feedbackType ->
                    analyticsLogger.logFeedbackEvent(
                        feedbackType = feedbackType,
                        feedbackKey = feedbackEventKey,
                        timestampMs = frame.timestampMs
                    )
                }
            }
            val feedbackTypeForUi = if (speechResult != null) {
                analysis.feedbackEvent
            } else {
                null
            }
            analysis.frameMetrics?.let { metrics ->
                metrics.transition?.let { transition ->
                    analyticsLogger.logTransition(transition, metrics)
                }
                logMetricsState(metrics, frame.timestampMs)
            }
            analysis.warningEvent?.let { event ->
                analyticsLogger.logWarningEvent(event)
            }
            val updatedState = currentState.copy(
                repCount = analysis.repCount.value,
                feedbackType = feedbackTypeForUi ?: currentState.feedbackType,
                feedbackKeys = listOfNotNull(analysis.feedbackEventKey),
                feedbackResId = if (exerciseType == ExerciseType.LUNGE) {
                    speechResult?.feedbackResId ?: currentState.feedbackResId
                } else {
                    FeedbackStringMapper.feedbackResId(
                        exerciseType = exerciseType,
                        feedbackType = feedbackTypeForUi ?: analysis.feedback.type,
                        feedbackKey = feedbackEventKey
                    )
                },
                accuracy = analysis.feedback.accuracy,
                isPerfectForm = analysis.feedback.isPerfectForm,
                poseFrame = frame,
                frameMetrics = analysis.frameMetrics,
                repSummary = analysis.repSummary,
                lungeDebugInfo = analysis.lungeDebugInfo,
                lastLungeRepSnapshot = updateLungeSnapshot(
                    analysis = analysis,
                    exerciseType = exerciseType,
                    timestampMs = frame.timestampMs,
                    current = currentState.lastLungeRepSnapshot
                )
            )
            PoseFrameProcessingResult(
                uiState = updatedState,
                speechEvent = speechResult?.speechEvent
            )
        }

    private fun updateLungeSnapshot(
        analysis: com.jeong.runninggoaltracker.domain.model.PoseAnalysisResult,
        exerciseType: ExerciseType,
        timestampMs: Long,
        current: LungeRepSnapshot?
    ): LungeRepSnapshot? {
        if (exerciseType != ExerciseType.LUNGE || !analysis.repCount.isIncremented) {
            return current
        }
        val summary = analysis.lungeRepSummary
        val debugInfo = analysis.lungeDebugInfo
        val feedbackKeys = summary?.feedbackKeys ?: emptyList()
        val goodFormReason = when {
            summary == null -> LungeGoodFormReason.NO_SUMMARY
            feedbackKeys.isNotEmpty() -> LungeGoodFormReason.FEEDBACK_KEYS_PRESENT
            else -> LungeGoodFormReason.GOOD_FORM
        }
        return LungeRepSnapshot(
            timestampMs = timestampMs,
            activeSide = debugInfo?.activeSide,
            countingSide = debugInfo?.countingSide,
            feedbackType = analysis.feedbackEvent,
            feedbackEventKey = analysis.feedbackEventKey,
            feedbackKeys = feedbackKeys,
            overallScore = summary?.overallScore,
            frontKneeMinAngle = summary?.frontKneeMinAngle,
            backKneeMinAngle = summary?.backKneeMinAngle,
            maxTorsoLeanAngle = summary?.maxTorsoLeanAngle,
            stabilityStdDev = summary?.stabilityStdDev,
            maxKneeForwardRatio = summary?.maxKneeForwardRatio,
            maxKneeCollapseRatio = summary?.maxKneeCollapseRatio,
            goodFormReason = goodFormReason
        )
    }

    private fun logMetricsState(metrics: SquatFrameMetrics, timestampMs: Long) {
        val attemptActive = metrics.attemptActive
        val previousAttempt = lastAttemptActive
        if (previousAttempt == null) {
            if (attemptActive) {
                analyticsLogger.logAttemptStart(metrics, timestampMs)
            }
        } else if (previousAttempt != attemptActive) {
            if (attemptActive) {
                analyticsLogger.logAttemptStart(metrics, timestampMs)
            } else {
                analyticsLogger.logAttemptEnd(metrics, timestampMs)
            }
        }
        lastAttemptActive = attemptActive
        val depthReached = metrics.depthReached
        val previousDepthReached = lastDepthReached
        if (depthReached && previousDepthReached != true) {
            analyticsLogger.logDepthReached(metrics, timestampMs)
        }
        lastDepthReached = depthReached
        val fullBodyVisible = metrics.fullBodyVisible
        val previousFullBodyVisible = lastFullBodyVisible
        if (previousFullBodyVisible == null || previousFullBodyVisible != fullBodyVisible) {
            analyticsLogger.logFullBodyVisibility(metrics, timestampMs)
        }
        lastFullBodyVisible = fullBodyVisible
    }

    fun overlayModeFor(exerciseType: ExerciseType): DebugOverlayMode = when (exerciseType) {
        ExerciseType.LUNGE -> DebugOverlayMode.LUNGE
        else -> DebugOverlayMode.GENERAL
    }
}

data class PoseFrameProcessingResult(
    val uiState: SmartWorkoutUiState,
    val speechEvent: com.jeong.runninggoaltracker.feature.ai_coach.presentation.SmartWorkoutSpeechEvent?
)
