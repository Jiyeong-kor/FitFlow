package com.jeong.runninggoaltracker.feature.ai_coach.processing

import androidx.camera.core.ImageAnalysis
import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.SquatFrameMetrics
import com.jeong.runninggoaltracker.domain.usecase.ProcessPoseUseCase
import com.jeong.runninggoaltracker.feature.ai_coach.contract.SmartWorkoutLogContract
import com.jeong.runninggoaltracker.feature.ai_coach.data.pose.PoseDetector
import com.jeong.runninggoaltracker.feature.ai_coach.logging.SmartWorkoutLogger
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
    private var isLastAttemptActive: Boolean? = null
    private var isLastDepthReached: Boolean? = null
    private var isLastFullBodyVisible: Boolean? = null

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
            if (analysis.isLowConfidenceSkipped) {
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
            logDebugFrame(
                frameTimestampMs = frame.timestampMs,
                exerciseType = exerciseType,
                analysis = analysis
            )
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
        val isAttemptActive = metrics.isAttemptActive
        val previousAttempt = isLastAttemptActive
        if (previousAttempt == null) {
            if (isAttemptActive) {
                analyticsLogger.logAttemptStart(metrics, timestampMs)
            }
        } else if (previousAttempt != isAttemptActive) {
            if (isAttemptActive) {
                analyticsLogger.logAttemptStart(metrics, timestampMs)
            } else {
                analyticsLogger.logAttemptEnd(metrics, timestampMs)
            }
        }
        isLastAttemptActive = isAttemptActive
        val isDepthReached = metrics.isDepthReached
        val previousDepthReached = isLastDepthReached
        if (isDepthReached && previousDepthReached != true) {
            analyticsLogger.logDepthReached(metrics, timestampMs)
        }
        isLastDepthReached = isDepthReached
        val isFullBodyVisible = metrics.isFullBodyVisible
        val previousFullBodyVisible = isLastFullBodyVisible
        if (previousFullBodyVisible == null || previousFullBodyVisible != isFullBodyVisible) {
            analyticsLogger.logFullBodyVisibility(metrics, timestampMs)
        }
        isLastFullBodyVisible = isFullBodyVisible
    }

    private fun logDebugFrame(
        frameTimestampMs: Long,
        exerciseType: ExerciseType,
        analysis: com.jeong.runninggoaltracker.domain.model.PoseAnalysisResult
    ) {
        SmartWorkoutLogger.logDebug {
            buildString {
                append("frameAnalysis")
                append(" timestamp=")
                append(frameTimestampMs)
                append(" exercise=")
                append(exerciseType.name)
                append(" rep=")
                append(analysis.repCount.value)
                append(" feedbackType=")
                append(analysis.feedback.type.name)
                append(" feedbackKey=")
                append(analysis.feedbackEventKey ?: "none")
                append(" accuracy=")
                append(analysis.feedback.accuracy)
                append(" perfect=")
                append(analysis.feedback.isPerfectForm)
                analysis.frameMetrics?.let { metrics ->
                    append(" phase=")
                    append(metrics.phase.name)
                    append(" side=")
                    append(metrics.side.name)
                    append(" kneeRaw=")
                    append(metrics.kneeAngleRaw)
                    append(" kneeEma=")
                    append(metrics.kneeAngleEma)
                    append(" trunkTiltRaw=")
                    append(metrics.trunkTiltVerticalAngleRaw)
                    append(" trunkTiltEma=")
                    append(metrics.trunkTiltVerticalAngleEma)
                    append(" trunkToThighRaw=")
                    append(metrics.trunkToThighAngleRaw)
                    append(" trunkToThighEma=")
                    append(metrics.trunkToThighAngleEma)
                    append(" attemptActive=")
                    append(metrics.isAttemptActive)
                    append(" depthReached=")
                    append(metrics.isDepthReached)
                    append(" fullBodyVisible=")
                    append(metrics.isFullBodyVisible)
                }
            }
        }
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
