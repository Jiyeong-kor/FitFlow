package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.contract.SQUAT_FLOAT_ZERO
import com.jeong.fitflow.domain.contract.SQUAT_INT_ZERO
import com.jeong.fitflow.domain.model.ExerciseType
import com.jeong.fitflow.domain.model.PoseAnalysisResult
import com.jeong.fitflow.domain.model.PoseFrame
import com.jeong.fitflow.domain.model.PostureFeedback
import com.jeong.fitflow.domain.model.PostureFeedbackType
import com.jeong.fitflow.domain.model.RepCount
import com.jeong.fitflow.domain.usecase.lunge.LungeAnalyzer
import com.jeong.fitflow.domain.usecase.squat.SquatAnalyzer

interface ExerciseAnalyzer {
    fun analyze(frame: PoseFrame): PoseAnalysisResult
}

class ProcessPoseUseCase(
    lungeAnalyzer: LungeAnalyzer,
    squatAnalyzer: SquatAnalyzer,
    pushUpAnalyzer: ExerciseAnalyzer
) {
    private val analyzers: Map<ExerciseType, ExerciseAnalyzer> = mapOf(
        ExerciseType.SQUAT to squatAnalyzer,
        ExerciseType.LUNGE to lungeAnalyzer,
        ExerciseType.PUSH_UP to pushUpAnalyzer
    )

    fun analyze(frame: PoseFrame, exerciseType: ExerciseType): PoseAnalysisResult =
        analyzers[exerciseType]?.analyze(frame)
            ?: PoseAnalysisResult(
                repCount = RepCount(SQUAT_INT_ZERO, isIncremented = false),
                feedback = PostureFeedback(
                    type = PostureFeedbackType.UNKNOWN,
                    isValid = false,
                    accuracy = SQUAT_FLOAT_ZERO,
                    isPerfectForm = false
                ),
                feedbackEvent = null,
                feedbackEventKey = null,
                frameMetrics = null,
                repSummary = null,
                lungeRepSummary = null,
                warningEvent = null,
                feedbackKeys = emptyList(),
                isLowConfidenceSkipped = false
            )
}
