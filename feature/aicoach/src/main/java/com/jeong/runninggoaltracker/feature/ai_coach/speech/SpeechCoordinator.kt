package com.jeong.runninggoaltracker.feature.ai_coach.speech

import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.PostureFeedbackType
import com.jeong.runninggoaltracker.feature.ai_coach.presentation.SmartWorkoutSpeechEvent

interface SpeechCoordinator {
    fun updateCooldown(cooldownMs: Long)
    fun reset()
    fun handleFeedback(
        feedbackType: PostureFeedbackType,
        feedbackEventKey: String?,
        exerciseType: ExerciseType,
        timestampMs: Long
    ): SpeechFeedbackResult?
}

data class SpeechFeedbackResult(
    val speechEvent: SmartWorkoutSpeechEvent,
    val feedbackResId: Int
)
