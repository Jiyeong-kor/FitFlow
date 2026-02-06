package com.jeong.runninggoaltracker.feature.goal.presentation

import com.jeong.runninggoaltracker.domain.usecase.GetRunningGoalUseCase
import com.jeong.runninggoaltracker.feature.goal.contract.GOAL_STATE_TIMEOUT_MS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GoalStateHolder(
    scope: CoroutineScope,
    getRunningGoalUseCase: GetRunningGoalUseCase,
    private val goalSaveHandler: GoalSaveHandler
) {

    private val inputState = MutableStateFlow(GoalInputState())
    private val goalFlow = getRunningGoalUseCase()

    val uiState: StateFlow<GoalUiState> = combine(goalFlow, inputState) { goal, input ->
        GoalUiState(
            currentGoalKm = goal?.weeklyGoalKm,
            weeklyGoalKmInput = input.weeklyGoalKmInput,
            error = input.error
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(GOAL_STATE_TIMEOUT_MS),
        initialValue = GoalUiState()
    )

    init {
        scope.launch {
            goalFlow.firstOrNull()?.let { goal ->
                inputState.update { current ->
                    current.copy(weeklyGoalKmInput = goal.weeklyGoalKm)
                }
            }
        }
    }

    fun onWeeklyGoalChanged(value: Double) {
        inputState.update { current ->
            current.copy(
                weeklyGoalKmInput = value,
                error = null
            )
        }
    }

    suspend fun saveGoal(onSuccess: () -> Unit) {
        val updatedState = goalSaveHandler.saveGoal(inputState.value, onSuccess)
        inputState.value = updatedState
    }
}
