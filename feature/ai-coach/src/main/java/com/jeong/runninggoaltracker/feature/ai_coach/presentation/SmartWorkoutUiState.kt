package com.jeong.runninggoaltracker.feature.ai_coach.presentation

import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.PoseFrame
import com.jeong.runninggoaltracker.domain.model.PostureFeedbackType
import com.jeong.runninggoaltracker.domain.model.SquatFrameMetrics
import com.jeong.runninggoaltracker.domain.model.SquatRepSummary


data class SmartWorkoutUiState(
    val exerciseType: ExerciseType = ExerciseType.SQUAT,
    val repCount: Int = 0,
    val feedbackType: PostureFeedbackType = PostureFeedbackType.UNKNOWN,
    val accuracy: Float = 0f,
    val isPerfectForm: Boolean = false,
    val poseFrame: PoseFrame? = null,
    val frameMetrics: SquatFrameMetrics? = null,
    val repSummary: SquatRepSummary? = null
)
