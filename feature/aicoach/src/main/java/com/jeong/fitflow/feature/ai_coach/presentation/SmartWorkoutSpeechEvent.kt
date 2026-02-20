package com.jeong.fitflow.feature.ai_coach.presentation

import androidx.annotation.StringRes
import com.jeong.fitflow.domain.model.ExerciseType
import com.jeong.fitflow.domain.model.PostureFeedbackType

data class SmartWorkoutSpeechEvent(
    val feedbackType: PostureFeedbackType,
    @field:StringRes val feedbackResId: Int,
    val exerciseType: ExerciseType
)
