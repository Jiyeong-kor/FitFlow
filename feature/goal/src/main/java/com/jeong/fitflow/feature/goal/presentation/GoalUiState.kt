package com.jeong.fitflow.feature.goal.presentation


data class GoalUiState(
    val currentGoalKm: Double? = null,
    val weeklyGoalKmInput: Double? = null,
    val error: GoalInputError? = null
)

enum class GoalInputError {
    INVALID_NUMBER,
    NON_POSITIVE
}

internal data class GoalInputState(
    val weeklyGoalKmInput: Double? = null,
    val error: GoalInputError? = null
)
