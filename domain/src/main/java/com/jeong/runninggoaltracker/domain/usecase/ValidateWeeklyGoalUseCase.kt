package com.jeong.runninggoaltracker.domain.usecase

sealed interface WeeklyGoalValidationResult {

    data class Valid(
        val weeklyGoalKm: Double
    ) : WeeklyGoalValidationResult

    enum class Error : WeeklyGoalValidationResult {
        INVALID_NUMBER,
        NON_POSITIVE
    }
}

class ValidateWeeklyGoalUseCase {

    operator fun invoke(input: String): WeeklyGoalValidationResult {
        val weeklyGoalKm = input.toDoubleOrNull()
            ?: return WeeklyGoalValidationResult.Error.INVALID_NUMBER

        return if (weeklyGoalKm > 0.0) {
            WeeklyGoalValidationResult.Valid(weeklyGoalKm = weeklyGoalKm)
        } else {
            WeeklyGoalValidationResult.Error.NON_POSITIVE
        }
    }
}
