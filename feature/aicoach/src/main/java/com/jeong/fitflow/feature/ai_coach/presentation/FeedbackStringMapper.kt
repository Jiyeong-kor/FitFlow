package com.jeong.fitflow.feature.ai_coach.presentation

import androidx.annotation.StringRes
import com.jeong.fitflow.domain.contract.LUNGE_DEPTH_TOO_DEEP_FRONT
import com.jeong.fitflow.domain.contract.LUNGE_DEPTH_TOO_SHALLOW_BACK
import com.jeong.fitflow.domain.contract.LUNGE_DEPTH_TOO_SHALLOW_FRONT
import com.jeong.fitflow.domain.contract.LUNGE_KNEE_COLLAPSE_INWARD
import com.jeong.fitflow.domain.contract.LUNGE_KNEE_TOO_FORWARD
import com.jeong.fitflow.domain.contract.LUNGE_TORSO_TOO_LEAN_FORWARD
import com.jeong.fitflow.domain.contract.LUNGE_UNSTABLE
import com.jeong.fitflow.domain.model.ExerciseType
import com.jeong.fitflow.domain.model.PostureFeedbackType
import com.jeong.fitflow.feature.ai_coach.R

object FeedbackStringMapper {
    @StringRes
    fun feedbackResId(
        exerciseType: ExerciseType,
        feedbackType: PostureFeedbackType,
        feedbackKey: String?
    ): Int =
        if (exerciseType == ExerciseType.LUNGE && feedbackKey != null) {
            lungeFeedbackTextResId(feedbackKey)
        } else {
            postureFeedbackTextResId(feedbackType)
        }

    @StringRes
    private fun postureFeedbackTextResId(type: PostureFeedbackType): Int = when (type) {
        PostureFeedbackType.GOOD_FORM -> R.string.smart_workout_feedback_good
        PostureFeedbackType.EXCESS_FORWARD_LEAN -> R.string.smart_workout_feedback_forward_lean
        PostureFeedbackType.HEEL_RISE -> R.string.smart_workout_feedback_heel_rise
        PostureFeedbackType.KNEE_FORWARD -> R.string.smart_workout_feedback_knee_forward
        PostureFeedbackType.TOO_SHALLOW -> R.string.smart_workout_feedback_shallow
        PostureFeedbackType.STAND_TALL -> R.string.smart_workout_feedback_stand_tall
        PostureFeedbackType.NOT_IN_FRAME -> R.string.smart_workout_feedback_not_in_frame
        PostureFeedbackType.UNKNOWN -> R.string.smart_workout_feedback_unknown
    }

    @StringRes
    private fun lungeFeedbackTextResId(key: String): Int = when (key) {
        LUNGE_DEPTH_TOO_SHALLOW_FRONT -> R.string.smart_workout_feedback_lunge_shallow_front
        LUNGE_DEPTH_TOO_DEEP_FRONT -> R.string.smart_workout_feedback_lunge_deep_front
        LUNGE_DEPTH_TOO_SHALLOW_BACK -> R.string.smart_workout_feedback_lunge_shallow_back
        LUNGE_KNEE_TOO_FORWARD -> R.string.smart_workout_feedback_lunge_knee_forward
        LUNGE_TORSO_TOO_LEAN_FORWARD -> R.string.smart_workout_feedback_lunge_torso_lean
        LUNGE_KNEE_COLLAPSE_INWARD -> R.string.smart_workout_feedback_lunge_knee_collapse
        LUNGE_UNSTABLE -> R.string.smart_workout_feedback_lunge_unstable
        else -> R.string.smart_workout_feedback_unknown
    }
}
