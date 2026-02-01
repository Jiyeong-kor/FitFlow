package com.jeong.runninggoaltracker.feature.ai_coach.presentation

import com.jeong.runninggoaltracker.domain.contract.SQUAT_FLOAT_ZERO
import com.jeong.runninggoaltracker.domain.contract.SQUAT_INT_ZERO
import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.PostureFeedbackType
import com.jeong.runninggoaltracker.feature.ai_coach.processing.PoseFrameProcessor
import javax.inject.Inject

class SmartWorkoutStateReducer @Inject constructor(
    private val poseFrameProcessor: PoseFrameProcessor
) {
    fun toggleOverlay(current: SmartWorkoutUiState): SmartWorkoutUiState {
        val nextMode = if (current.overlayMode == DebugOverlayMode.OFF) {
            poseFrameProcessor.overlayModeFor(current.exerciseType)
        } else {
            DebugOverlayMode.OFF
        }
        return current.copy(overlayMode = nextMode)
    }

    fun updateExerciseType(
        current: SmartWorkoutUiState,
        exerciseType: ExerciseType
    ): SmartWorkoutUiState =
        if (current.exerciseType == exerciseType) {
            current
        } else {
            current.copy(
                exerciseType = exerciseType,
                repCount = SQUAT_INT_ZERO,
                feedbackType = PostureFeedbackType.UNKNOWN,
                feedbackKeys = emptyList(),
                accuracy = SQUAT_FLOAT_ZERO,
                isPerfectForm = false,
                overlayMode = if (current.overlayMode == DebugOverlayMode.OFF) {
                    current.overlayMode
                } else {
                    poseFrameProcessor.overlayModeFor(exerciseType)
                }
            )
        }
}
