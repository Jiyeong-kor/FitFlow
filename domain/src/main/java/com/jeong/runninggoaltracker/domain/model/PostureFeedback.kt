package com.jeong.runninggoaltracker.domain.model

enum class PostureFeedbackType {
    GOOD_FORM,
    TOO_SHALLOW,
    STAND_TALL,
    UNKNOWN
}

data class PostureFeedback(
    val type: PostureFeedbackType,
    val isValid: Boolean,
    val accuracy: Float,
    val isPerfectForm: Boolean
)
