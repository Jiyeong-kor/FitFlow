package com.jeong.runninggoaltracker.feature.ai_coach.speech

import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.PostureFeedbackType
import com.jeong.runninggoaltracker.feature.ai_coach.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultSpeechCoordinatorTest {

    @Test
    fun handleFeedback_emits_for_squat_even_when_feedback_key_is_null() {
        val coordinator = DefaultSpeechCoordinator()

        val result = coordinator.handleFeedback(
            feedbackType = PostureFeedbackType.KNEE_FORWARD,
            feedbackEventKey = null,
            exerciseType = ExerciseType.SQUAT,
            timestampMs = 1_000L
        )

        assertNotNull(result)
        assertEquals(R.string.smart_workout_feedback_knee_forward, result?.feedbackResId)
        assertEquals(ExerciseType.SQUAT, result?.speechEvent?.exerciseType)
    }

    @Test
    fun handleFeedback_respects_cooldown_for_repeated_same_squat_feedback() {
        val coordinator = DefaultSpeechCoordinator()

        val first = coordinator.handleFeedback(
            feedbackType = PostureFeedbackType.STAND_TALL,
            feedbackEventKey = null,
            exerciseType = ExerciseType.SQUAT,
            timestampMs = 1_000L
        )
        val suppressed = coordinator.handleFeedback(
            feedbackType = PostureFeedbackType.STAND_TALL,
            feedbackEventKey = null,
            exerciseType = ExerciseType.SQUAT,
            timestampMs = 1_500L
        )

        assertNotNull(first)
        assertNull(suppressed)
    }
}
