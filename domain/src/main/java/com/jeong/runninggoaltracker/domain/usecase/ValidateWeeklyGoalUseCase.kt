package com.jeong.runninggoaltracker.domain.usecase

import javax.inject.Inject

sealed interface WeeklyGoalValidationResult {

    data class Valid(
        val weeklyGoalKm: Double
    ) : WeeklyGoalValidationResult

    enum class Error : WeeklyGoalValidationResult {
        INVALID_NUMBER,
        NON_POSITIVE
    }
}

class ValidateWeeklyGoalUseCase @Inject constructor() {

    operator fun invoke(input: Double?): WeeklyGoalValidationResult {
        val weeklyGoalKm = input
            ?: return WeeklyGoalValidationResult.Error.INVALID_NUMBER

        return if (weeklyGoalKm > 0.0) {
            WeeklyGoalValidationResult.Valid(weeklyGoalKm = weeklyGoalKm)
        } else {
            WeeklyGoalValidationResult.Error.NON_POSITIVE
        }
    }
}
