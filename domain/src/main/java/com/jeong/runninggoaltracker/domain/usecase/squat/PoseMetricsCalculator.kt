package com.jeong.runninggoaltracker.domain.usecase.squat

import com.jeong.runninggoaltracker.domain.contract.SQUAT_CONFIDENCE_DIVISOR
import com.jeong.runninggoaltracker.domain.contract.SQUAT_FLOAT_ONE
import com.jeong.runninggoaltracker.domain.contract.SQUAT_FLOAT_ZERO
import com.jeong.runninggoaltracker.domain.contract.SQUAT_INT_ONE
import com.jeong.runninggoaltracker.domain.contract.SQUAT_INT_ZERO
import com.jeong.runninggoaltracker.domain.contract.SQUAT_MIN_LANDMARK_CONFIDENCE
import com.jeong.runninggoaltracker.domain.model.PoseCalibration
import com.jeong.runninggoaltracker.domain.model.PoseFrame
import com.jeong.runninggoaltracker.domain.model.PoseLandmark
import com.jeong.runninggoaltracker.domain.model.PoseLandmarkType
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

data class PoseRawSquatMetrics(
    val kneeAngle: Float,
    val trunkLeanAngle: Float,
    val heelRiseRatio: Float?,
    val kneeForwardRatio: Float?,
    val legLength: Float,
    val ankleY: Float,
    val kneeX: Float
)

class PoseMetricsCalculator(
    private val minConfidence: Float = SQUAT_MIN_LANDMARK_CONFIDENCE
) {
    fun calculate(frame: PoseFrame, calibration: PoseCalibration?): PoseRawSquatMetrics? {
        val side = selectSideLandmarks(frame) ?: return null
        val kneeAngle = calculateAngle(side.hip, side.knee, side.ankle) ?: return null
        val trunkLeanAngle = calculateTrunkLean(side.shoulder, side.hip) ?: return null
        val legLength = distance(side.hip, side.ankle)
        val normalizedLength = calibration?.baselineLegLength ?: legLength
        val heelRiseRatio =
            calibration?.let { ratio(it.baselineAnkleY - side.ankle.y, normalizedLength) }
        val kneeForwardRatio =
            calibration?.let { ratio(abs(side.knee.x - side.ankle.x), normalizedLength) }
        return PoseRawSquatMetrics(
            kneeAngle = kneeAngle,
            trunkLeanAngle = trunkLeanAngle,
            heelRiseRatio = heelRiseRatio,
            kneeForwardRatio = kneeForwardRatio,
            legLength = legLength,
            ankleY = side.ankle.y,
            kneeX = side.knee.x
        )
    }

    private fun selectSideLandmarks(frame: PoseFrame): SideLandmarks? {
        val left = createSideLandmarks(
            frame.landmark(PoseLandmarkType.LEFT_SHOULDER),
            frame.landmark(PoseLandmarkType.LEFT_HIP),
            frame.landmark(PoseLandmarkType.LEFT_KNEE),
            frame.landmark(PoseLandmarkType.LEFT_ANKLE)
        )
        val right = createSideLandmarks(
            frame.landmark(PoseLandmarkType.RIGHT_SHOULDER),
            frame.landmark(PoseLandmarkType.RIGHT_HIP),
            frame.landmark(PoseLandmarkType.RIGHT_KNEE),
            frame.landmark(PoseLandmarkType.RIGHT_ANKLE)
        )
        return when {
            left != null && right != null -> if (left.averageConfidence >= right.averageConfidence) left else right
            left != null -> left
            right != null -> right
            else -> null
        }
    }

    private fun createSideLandmarks(
        shoulder: PoseLandmark?,
        hip: PoseLandmark?,
        knee: PoseLandmark?,
        ankle: PoseLandmark?
    ): SideLandmarks? {
        if (shoulder == null || hip == null || knee == null || ankle == null) return null
        val minConfidence = listOf(shoulder, hip, knee, ankle).minOf { it.confidence }
        return if (minConfidence >= this.minConfidence) {
            SideLandmarks(
                shoulder,
                hip,
                knee,
                ankle,
                averageConfidence = averageConfidence(shoulder, hip, knee, ankle)
            )
        } else {
            null
        }
    }

    private fun averageConfidence(
        shoulder: PoseLandmark,
        hip: PoseLandmark,
        knee: PoseLandmark,
        ankle: PoseLandmark
    ): Float =
        (shoulder.confidence + hip.confidence + knee.confidence + ankle.confidence) / SQUAT_CONFIDENCE_DIVISOR

    private fun calculateTrunkLean(shoulder: PoseLandmark, hip: PoseLandmark): Float? {
        val vectorX = hip.x - shoulder.x
        val vectorY = hip.y - shoulder.y
        val vectorMagnitude = sqrt(vectorX * vectorX + vectorY * vectorY)
        if (vectorMagnitude == SQUAT_FLOAT_ZERO) return null
        val dot = vectorY
        val cosValue = (dot / vectorMagnitude).coerceIn(-SQUAT_FLOAT_ONE, SQUAT_FLOAT_ONE)
        return Math.toDegrees(acos(cosValue).toDouble()).toFloat()
    }

    private fun calculateAngle(
        first: PoseLandmark,
        middle: PoseLandmark,
        last: PoseLandmark
    ): Float? {
        val vectorA = floatArrayOf(first.x - middle.x, first.y - middle.y)
        val vectorB = floatArrayOf(last.x - middle.x, last.y - middle.y)
        val dot =
            vectorA[SQUAT_INT_ZERO] * vectorB[SQUAT_INT_ZERO] + vectorA[SQUAT_INT_ONE] * vectorB[SQUAT_INT_ONE]
        val normA =
            sqrt(vectorA[SQUAT_INT_ZERO] * vectorA[SQUAT_INT_ZERO] + vectorA[SQUAT_INT_ONE] * vectorA[SQUAT_INT_ONE])
        val normB =
            sqrt(vectorB[SQUAT_INT_ZERO] * vectorB[SQUAT_INT_ZERO] + vectorB[SQUAT_INT_ONE] * vectorB[SQUAT_INT_ONE])
        if (normA == SQUAT_FLOAT_ZERO || normB == SQUAT_FLOAT_ZERO) return null
        val cosValue = (dot / (normA * normB)).coerceIn(-SQUAT_FLOAT_ONE, SQUAT_FLOAT_ONE)
        return Math.toDegrees(acos(cosValue).toDouble()).toFloat()
    }

    private fun distance(first: PoseLandmark, last: PoseLandmark): Float {
        val dx = first.x - last.x
        val dy = first.y - last.y
        return sqrt(dx * dx + dy * dy)
    }

    private fun ratio(numerator: Float, denominator: Float): Float? =
        if (denominator == SQUAT_FLOAT_ZERO) null else numerator / denominator
}

data class SideLandmarks(
    val shoulder: PoseLandmark,
    val hip: PoseLandmark,
    val knee: PoseLandmark,
    val ankle: PoseLandmark,
    val averageConfidence: Float
)
