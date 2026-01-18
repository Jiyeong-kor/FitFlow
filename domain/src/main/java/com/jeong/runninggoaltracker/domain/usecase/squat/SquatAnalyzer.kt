package com.jeong.runninggoaltracker.domain.usecase.squat

import com.jeong.runninggoaltracker.domain.contract.SQUAT_CALIBRATION_REQUIRED_FRAMES
import com.jeong.runninggoaltracker.domain.contract.SQUAT_FLOAT_ONE
import com.jeong.runninggoaltracker.domain.contract.SQUAT_FLOAT_ZERO
import com.jeong.runninggoaltracker.domain.contract.SQUAT_GOOD_DEPTH_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.SQUAT_INT_ONE
import com.jeong.runninggoaltracker.domain.contract.SQUAT_INT_ZERO
import com.jeong.runninggoaltracker.domain.contract.SQUAT_MAX_TRUNK_LEAN_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.SQUAT_SHALLOW_DEPTH_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.SQUAT_STANDING_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.model.PoseAnalysisResult
import com.jeong.runninggoaltracker.domain.model.PoseCalibration
import com.jeong.runninggoaltracker.domain.model.PoseFrame
import com.jeong.runninggoaltracker.domain.model.PostureFeedback
import com.jeong.runninggoaltracker.domain.model.PostureFeedbackType
import com.jeong.runninggoaltracker.domain.model.RepCount
import com.jeong.runninggoaltracker.domain.model.SquatFrameMetrics
import com.jeong.runninggoaltracker.domain.model.SquatRepSummary
import com.jeong.runninggoaltracker.domain.model.SquatState
import com.jeong.runninggoaltracker.domain.usecase.ExerciseAnalyzer
import kotlin.math.max
import kotlin.math.min

class SquatAnalyzer(
    private val metricsCalculator: PoseMetricsCalculator = PoseMetricsCalculator(),
    private val stateMachine: SquatStateMachine = SquatStateMachine(),
    private val scorer: SquatFormScorer = SquatFormScorer()
) : ExerciseAnalyzer {
    private var repCount: Int = SQUAT_INT_ZERO
    private var calibration: PoseCalibration? = null
    private var calibrationFrames: Int = SQUAT_INT_ZERO
    private var calibrationKneeSum: Float = SQUAT_FLOAT_ZERO
    private var calibrationTrunkSum: Float = SQUAT_FLOAT_ZERO
    private var calibrationLegSum: Float = SQUAT_FLOAT_ZERO
    private var calibrationAnkleSum: Float = SQUAT_FLOAT_ZERO
    private var calibrationKneeXSum: Float = SQUAT_FLOAT_ZERO
    private val kneeFilter = EmaFilter()
    private val trunkFilter = EmaFilter()
    private var lastMetrics: PoseRawSquatMetrics? = null
    private var repMinKneeAngle: Float? = null
    private var repMaxTrunkLean: Float? = null
    private var repMaxHeelRise: Float? = null
    private var repMaxKneeForward: Float? = null
    private var previousState: SquatState = SquatState.STANDING

    override fun analyze(frame: PoseFrame): PoseAnalysisResult {
        val rawMetrics = metricsCalculator.calculate(frame, calibration)
        val isReliable = rawMetrics != null
        val metrics = rawMetrics ?: lastMetrics
        if (rawMetrics != null) {
            lastMetrics = rawMetrics
        }
        val smoothedKnee = if (rawMetrics != null) {
            kneeFilter.update(rawMetrics.kneeAngle)
        } else {
            kneeFilter.current()
        }
        val smoothedTrunk = if (rawMetrics != null) {
            trunkFilter.update(rawMetrics.trunkLeanAngle)
        } else {
            trunkFilter.current()
        }
        if (smoothedKnee == null || smoothedTrunk == null) {
            return PoseAnalysisResult(
                repCount = RepCount(repCount, isIncremented = false),
                feedback = PostureFeedback(
                    type = PostureFeedbackType.UNKNOWN,
                    isValid = false,
                    accuracy = SQUAT_FLOAT_ZERO,
                    isPerfectForm = false
                ),
                frameMetrics = null,
                repSummary = null
            )
        }
        val stateResult = stateMachine.update(smoothedKnee, isReliable)
        if (calibration == null && isReliable && stateResult.state == SquatState.STANDING) {
            accumulateCalibration(rawMetrics)
        }
        val repSummary = handleRepTracking(stateResult, smoothedKnee, smoothedTrunk, metrics)
        previousState = stateResult.state
        val feedbackType = feedbackTypeFor(smoothedKnee, smoothedTrunk)
        val accuracy = accuracyFor(smoothedKnee)
        val isPerfectForm = feedbackType == PostureFeedbackType.GOOD_FORM
        val frameMetrics = SquatFrameMetrics(
            kneeAngle = smoothedKnee,
            trunkLeanAngle = smoothedTrunk,
            heelRiseRatio = metrics?.heelRiseRatio,
            kneeForwardRatio = metrics?.kneeForwardRatio,
            state = stateResult.state,
            isLandmarkReliable = isReliable,
            isCalibrated = calibration != null
        )
        return PoseAnalysisResult(
            repCount = RepCount(repCount, isIncremented = stateResult.repCompleted),
            feedback = PostureFeedback(
                type = feedbackType,
                isValid = feedbackType != PostureFeedbackType.TOO_SHALLOW,
                accuracy = accuracy,
                isPerfectForm = isPerfectForm
            ),
            frameMetrics = frameMetrics,
            repSummary = repSummary
        )
    }

    private fun accumulateCalibration(metrics: PoseRawSquatMetrics) {
        calibrationFrames += SQUAT_INT_ONE
        calibrationKneeSum += metrics.kneeAngle
        calibrationTrunkSum += metrics.trunkLeanAngle
        calibrationLegSum += metrics.legLength
        calibrationAnkleSum += metrics.ankleY
        calibrationKneeXSum += metrics.kneeX
        if (calibrationFrames >= SQUAT_CALIBRATION_REQUIRED_FRAMES) {
            val divisor = calibrationFrames.toFloat()
            calibration = PoseCalibration(
                baselineKneeAngle = calibrationKneeSum / divisor,
                baselineTrunkLeanAngle = calibrationTrunkSum / divisor,
                baselineLegLength = calibrationLegSum / divisor,
                baselineAnkleY = calibrationAnkleSum / divisor,
                baselineKneeX = calibrationKneeXSum / divisor
            )
        }
    }

    private fun handleRepTracking(
        stateResult: SquatStateMachineResult,
        kneeAngle: Float,
        trunkLean: Float,
        metrics: PoseRawSquatMetrics?
    ): SquatRepSummary? {
        if (stateResult.state == SquatState.DESCENDING && previousState != SquatState.DESCENDING) {
            resetRepTracking()
        }
        if (stateResult.state != SquatState.STANDING) {
            repMinKneeAngle = repMinKneeAngle?.let { min(it, kneeAngle) } ?: kneeAngle
            repMaxTrunkLean = repMaxTrunkLean?.let { max(it, trunkLean) } ?: trunkLean
            val heel = metrics?.heelRiseRatio
            if (heel != null) {
                repMaxHeelRise = repMaxHeelRise?.let { max(it, heel) } ?: heel
            }
            val kneeForward = metrics?.kneeForwardRatio
            if (kneeForward != null) {
                repMaxKneeForward = repMaxKneeForward?.let { max(it, kneeForward) } ?: kneeForward
            }
        }
        if (stateResult.repCompleted) {
            repCount += SQUAT_INT_ONE
            val minKnee = repMinKneeAngle ?: kneeAngle
            val maxTrunk = repMaxTrunkLean ?: trunkLean
            val summary = scorer.score(
                SquatRepMetrics(
                    minKneeAngle = minKnee,
                    maxTrunkLeanAngle = maxTrunk,
                    maxHeelRiseRatio = repMaxHeelRise,
                    maxKneeForwardRatio = repMaxKneeForward
                )
            )
            resetRepTracking()
            return summary
        }
        return null
    }

    private fun resetRepTracking() {
        repMinKneeAngle = null
        repMaxTrunkLean = null
        repMaxHeelRise = null
        repMaxKneeForward = null
    }

    private fun feedbackTypeFor(kneeAngle: Float, trunkLean: Float): PostureFeedbackType = when {
        kneeAngle <= SQUAT_GOOD_DEPTH_ANGLE_THRESHOLD && trunkLean <= SQUAT_MAX_TRUNK_LEAN_ANGLE_THRESHOLD ->
            PostureFeedbackType.GOOD_FORM

        kneeAngle <= SQUAT_SHALLOW_DEPTH_ANGLE_THRESHOLD -> PostureFeedbackType.TOO_SHALLOW
        else -> PostureFeedbackType.STAND_TALL
    }

    private fun accuracyFor(kneeAngle: Float): Float {
        val depthRange = SQUAT_STANDING_KNEE_ANGLE_THRESHOLD - SQUAT_GOOD_DEPTH_ANGLE_THRESHOLD
        val normalized = ((kneeAngle - SQUAT_GOOD_DEPTH_ANGLE_THRESHOLD) / depthRange)
            .coerceIn(SQUAT_FLOAT_ZERO, SQUAT_FLOAT_ONE)
        return (SQUAT_FLOAT_ONE - normalized).coerceIn(SQUAT_FLOAT_ZERO, SQUAT_FLOAT_ONE)
    }
}
