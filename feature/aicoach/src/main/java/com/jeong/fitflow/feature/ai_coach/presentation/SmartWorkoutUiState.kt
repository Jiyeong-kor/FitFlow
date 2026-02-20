package com.jeong.fitflow.feature.ai_coach.presentation

import androidx.annotation.StringRes
import com.jeong.fitflow.domain.contract.SQUAT_FLOAT_ZERO
import com.jeong.fitflow.domain.contract.SQUAT_INT_ZERO
import com.jeong.fitflow.domain.model.ExerciseType
import com.jeong.fitflow.domain.model.LungeDebugInfo
import com.jeong.fitflow.domain.model.PoseFrame
import com.jeong.fitflow.domain.model.PostureFeedbackType
import com.jeong.fitflow.domain.model.SquatFrameMetrics
import com.jeong.fitflow.domain.model.SquatRepSummary
import com.jeong.fitflow.feature.ai_coach.R

data class SmartWorkoutUiState(
    val exerciseType: ExerciseType = ExerciseType.SQUAT,
    val repCount: Int = SQUAT_INT_ZERO,
    val feedbackType: PostureFeedbackType = PostureFeedbackType.UNKNOWN,
    val feedbackKeys: List<String> = emptyList(),
    @field:StringRes val feedbackResId: Int = R.string.smart_workout_feedback_unknown,
    val accuracy: Float = SQUAT_FLOAT_ZERO,
    val isPerfectForm: Boolean = false,
    val poseFrame: PoseFrame? = null,
    val frameMetrics: SquatFrameMetrics? = null,
    val repSummary: SquatRepSummary? = null,
    val lungeDebugInfo: LungeDebugInfo? = null,
    val lastLungeRepSnapshot: LungeRepSnapshot? = null,
    val overlayMode: DebugOverlayMode = DebugOverlayMode.GENERAL
)
