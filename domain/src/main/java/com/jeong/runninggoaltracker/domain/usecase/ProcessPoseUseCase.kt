package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.PoseAnalysisResult
import com.jeong.runninggoaltracker.domain.model.PoseFrame
import com.jeong.runninggoaltracker.domain.model.PoseLandmark
import com.jeong.runninggoaltracker.domain.model.PoseLandmarkType
import com.jeong.runninggoaltracker.domain.model.PostureFeedback
import com.jeong.runninggoaltracker.domain.model.PostureFeedbackType
import com.jeong.runninggoaltracker.domain.model.RepCount
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

interface ExerciseAnalyzer {
    fun analyze(frame: PoseFrame): PoseAnalysisResult
}

class SquatAnalyzer : ExerciseAnalyzer {
    private var phase: SquatPhase = SquatPhase.STANDING
    private var repCount: Int = 0

    override fun analyze(frame: PoseFrame): PoseAnalysisResult {
        val kneeAngle = averageKneeAngle(frame)
        if (kneeAngle == null) {
            return PoseAnalysisResult(
                repCount = RepCount(repCount, isIncremented = false),
                feedback = PostureFeedback(
                    type = PostureFeedbackType.UNKNOWN,
                    isValid = false,
                    accuracy = 0f,
                    isPerfectForm = false
                )
            )
        }

        val isDown = kneeAngle <= 110f
        val isUp = kneeAngle >= 160f
        val incremented = if (isUp && phase == SquatPhase.SQUATTING) {
            phase = SquatPhase.STANDING
            repCount += 1
            true
        } else {
            if (isDown && phase == SquatPhase.STANDING) {
                phase = SquatPhase.SQUATTING
            }
            false
        }

        val feedbackType = when {
            kneeAngle <= 100f -> PostureFeedbackType.GOOD_FORM
            kneeAngle in 100f..140f -> PostureFeedbackType.TOO_SHALLOW
            else -> PostureFeedbackType.STAND_TALL
        }

        val accuracy = (1f - abs(kneeAngle - 95f) / 85f).coerceIn(0f, 1f)
        val isPerfectForm = feedbackType == PostureFeedbackType.GOOD_FORM

        return PoseAnalysisResult(
            repCount = RepCount(repCount, incremented),
            feedback = PostureFeedback(
                type = feedbackType,
                isValid = feedbackType != PostureFeedbackType.TOO_SHALLOW,
                accuracy = accuracy,
                isPerfectForm = isPerfectForm
            )
        )
    }

    private fun averageKneeAngle(frame: PoseFrame): Float? {
        val left = calculateAngle(
            frame.landmark(PoseLandmarkType.LEFT_HIP),
            frame.landmark(PoseLandmarkType.LEFT_KNEE),
            frame.landmark(PoseLandmarkType.LEFT_ANKLE)
        )
        val right = calculateAngle(
            frame.landmark(PoseLandmarkType.RIGHT_HIP),
            frame.landmark(PoseLandmarkType.RIGHT_KNEE),
            frame.landmark(PoseLandmarkType.RIGHT_ANKLE)
        )

        return when {
            left != null && right != null -> (left + right) / 2f
            left != null -> left
            right != null -> right
            else -> null
        }
    }

    private fun calculateAngle(
        first: PoseLandmark?,
        middle: PoseLandmark?,
        last: PoseLandmark?
    ): Float? {
        if (first == null || middle == null || last == null) return null
        val vectorA = floatArrayOf(first.x - middle.x, first.y - middle.y)
        val vectorB = floatArrayOf(last.x - middle.x, last.y - middle.y)
        val dot = vectorA[0] * vectorB[0] + vectorA[1] * vectorB[1]
        val normA = sqrt(vectorA[0] * vectorA[0] + vectorA[1] * vectorA[1])
        val normB = sqrt(vectorB[0] * vectorB[0] + vectorB[1] * vectorB[1])
        if (normA == 0f || normB == 0f) return null
        val cosValue = (dot / (normA * normB)).coerceIn(-1f, 1f)
        return Math.toDegrees(acos(cosValue).toDouble()).toFloat()
    }
}

class ProcessPoseUseCase @Inject constructor() {
    private val analyzers: Map<ExerciseType, ExerciseAnalyzer> = mapOf(
        ExerciseType.SQUAT to SquatAnalyzer(),
        ExerciseType.LUNGE to SquatAnalyzer(),
        ExerciseType.PUSH_UP to SquatAnalyzer()
    )

    fun analyze(frame: PoseFrame, exerciseType: ExerciseType): PoseAnalysisResult =
        analyzers[exerciseType]?.analyze(frame)
            ?: PoseAnalysisResult(
                repCount = RepCount(0, isIncremented = false),
                feedback = PostureFeedback(
                    type = PostureFeedbackType.UNKNOWN,
                    isValid = false,
                    accuracy = 0f,
                    isPerfectForm = false
                )
            )
}

enum class SquatPhase {
    STANDING,
    SQUATTING
}
