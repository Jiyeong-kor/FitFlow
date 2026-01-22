package com.jeong.runninggoaltracker.feature.ai_coach.presentation

import androidx.annotation.StringRes
import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.PostureFeedbackType

data class SmartWorkoutSpeechEvent(
    val feedbackType: PostureFeedbackType,
    @field:StringRes val feedbackResId: Int,
    val exerciseType: ExerciseType
)
