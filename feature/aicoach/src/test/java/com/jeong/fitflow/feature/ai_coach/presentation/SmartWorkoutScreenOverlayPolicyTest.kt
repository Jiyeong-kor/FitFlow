package com.jeong.fitflow.feature.ai_coach.presentation

import com.jeong.fitflow.domain.model.ExerciseType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartWorkoutScreenOverlayPolicyTest {

    @Test
    fun `랜드마크 오버레이는 푸시업에서만 표시된다`() {
        assertFalse(shouldShowLandmarkOverlay(ExerciseType.SQUAT))
        assertFalse(shouldShowLandmarkOverlay(ExerciseType.LUNGE))
        assertTrue(shouldShowLandmarkOverlay(ExerciseType.PUSH_UP))
    }
}
