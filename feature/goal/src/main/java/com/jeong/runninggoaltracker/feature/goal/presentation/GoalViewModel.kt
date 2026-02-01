package com.jeong.runninggoaltracker.feature.goal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.usecase.GetRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpsertRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.ValidateWeeklyGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.WeeklyGoalValidationResult
import com.jeong.runninggoaltracker.feature.goal.contract.GOAL_STATE_TIMEOUT_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

@HiltViewModel
class GoalViewModel @Inject constructor(
    getRunningGoalUseCase: GetRunningGoalUseCase,
    private val goalSaveHandler: GoalSaveHandler
) : ViewModel() {

    private val inputState = MutableStateFlow(GoalInputState())
    private val goalFlow = getRunningGoalUseCase()

    init {
        viewModelScope.launch {
            goalFlow.firstOrNull()?.let { goal ->
                inputState.update { current ->
                    current.copy(weeklyGoalKmInput = goal.weeklyGoalKm)
                }
            }
        }
    }

    val uiState: StateFlow<GoalUiState> = combine(goalFlow, inputState) { goal, input ->
        GoalUiState(
            currentGoalKm = goal?.weeklyGoalKm,
            weeklyGoalKmInput = input.weeklyGoalKmInput,
            error = input.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(GOAL_STATE_TIMEOUT_MS),
        initialValue = GoalUiState()
    )

    fun onWeeklyGoalChanged(value: Double) {
        inputState.update { current ->
            current.copy(
                weeklyGoalKmInput = value,
                error = null
            )
        }
    }

    fun saveGoal(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val updatedState = goalSaveHandler.saveGoal(inputState.value, onSuccess)
            inputState.value = updatedState
        }
    }
}

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
