package com.jeong.runninggoaltracker.feature.goal.presentation

import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.usecase.UpsertRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.ValidateWeeklyGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.WeeklyGoalValidationResult
import javax.inject.Inject

class GoalSaveHandler @Inject constructor(
    private val upsertRunningGoalUseCase: UpsertRunningGoalUseCase,
    private val validateWeeklyGoalUseCase: ValidateWeeklyGoalUseCase
) {
    internal suspend fun saveGoal(
        currentState: GoalInputState,
        onSuccess: () -> Unit
    ): GoalInputState =
        when (val result = validateWeeklyGoalUseCase(currentState.weeklyGoalKmInput)) {
            WeeklyGoalValidationResult.Error.INVALID_NUMBER ->
                currentState.copy(error = GoalInputError.INVALID_NUMBER)
            WeeklyGoalValidationResult.Error.NON_POSITIVE ->
                currentState.copy(error = GoalInputError.NON_POSITIVE)
            is WeeklyGoalValidationResult.Valid -> {
                upsertRunningGoalUseCase(RunningGoal(weeklyGoalKm = result.weeklyGoalKm))
                onSuccess()
                currentState.copy(error = null)
            }
        }
}
