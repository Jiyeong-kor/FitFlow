package com.jeong.fitflow.feature.ai_coach.speech

import com.jeong.fitflow.domain.model.ExerciseType
import com.jeong.fitflow.domain.model.PostureFeedbackType
import com.jeong.fitflow.feature.ai_coach.contract.SmartWorkoutSpeechContract
import com.jeong.fitflow.feature.ai_coach.presentation.FeedbackStringMapper
import com.jeong.fitflow.feature.ai_coach.presentation.SmartWorkoutSpeechEvent
import javax.inject.Inject

class DefaultSpeechCoordinator @Inject constructor() : SpeechCoordinator {
    private var lastSpokenType: PostureFeedbackType? = null
    private var lastSpokenKey: String? = null
    private var lastSpokenTimestampMs: Long = SmartWorkoutSpeechContract.DEFAULT_COOLDOWN_MS
    private var speechCooldownMs: Long = SmartWorkoutSpeechContract.DEFAULT_COOLDOWN_MS

    override fun updateCooldown(cooldownMs: Long) {
        speechCooldownMs = cooldownMs
    }

    override fun reset() {
        lastSpokenType = null
        lastSpokenKey = null
    }

    override fun handleFeedback(
        feedbackType: PostureFeedbackType,
        feedbackEventKey: String?,
        exerciseType: ExerciseType,
        timestampMs: Long
    ): SpeechFeedbackResult? {
        val key = feedbackEventKey ?: feedbackType.name
        val lastType = lastSpokenType
        val isChanged = lastType != feedbackType || lastSpokenKey != key
        val elapsedMs = timestampMs - lastSpokenTimestampMs
        val shouldEmit = if (exerciseType == ExerciseType.LUNGE && !isChanged) {
            elapsedMs >= speechCooldownMs
        } else {
            isChanged || elapsedMs >= speechCooldownMs
        }
        if (!shouldEmit) {
            return null
        }
        val feedbackResId = FeedbackStringMapper.feedbackResId(
            exerciseType = exerciseType,
            feedbackType = feedbackType,
            feedbackKey = feedbackEventKey
        )
        val speechEvent = SmartWorkoutSpeechEvent(
            feedbackType = feedbackType,
            feedbackResId = feedbackResId,
            exerciseType = exerciseType
        )
        lastSpokenType = feedbackType
        lastSpokenKey = key
        lastSpokenTimestampMs = timestampMs
        return SpeechFeedbackResult(speechEvent = speechEvent, feedbackResId = feedbackResId)
    }
}
