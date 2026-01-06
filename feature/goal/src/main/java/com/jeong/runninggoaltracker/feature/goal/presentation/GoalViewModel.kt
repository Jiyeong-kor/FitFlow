package com.jeong.runninggoaltracker.feature.goal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.model.formattedWeeklyGoalKm
import com.jeong.runninggoaltracker.domain.usecase.GetRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpsertRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.ValidateWeeklyGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.WeeklyGoalValidationResult
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
    val weeklyGoalInput: String = "",
    val error: GoalInputError? = null
)

enum class GoalInputError {
    INVALID_NUMBER,
    NON_POSITIVE
}

private data class GoalInputState(
    val weeklyGoalInput: String = "",
    val error: GoalInputError? = null
)

@HiltViewModel
class GoalViewModel @Inject constructor(
    getRunningGoalUseCase: GetRunningGoalUseCase,
    private val upsertRunningGoalUseCase: UpsertRunningGoalUseCase,
    private val validateWeeklyGoalUseCase: ValidateWeeklyGoalUseCase
) : ViewModel() {

    private val inputState = MutableStateFlow(GoalInputState())
    private val goalFlow = getRunningGoalUseCase()

    init {
        viewModelScope.launch {
            goalFlow.firstOrNull()?.let { goal ->
                inputState.update { current ->
                    current.copy(weeklyGoalInput = goal.formattedWeeklyGoalKm)
                }
            }
        }
    }

    val uiState: StateFlow<GoalUiState> = combine(goalFlow, inputState) { goal, input ->
        GoalUiState(
            currentGoalKm = goal?.weeklyGoalKm,
            weeklyGoalInput = input.weeklyGoalInput,
            error = input.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GoalUiState()
    )

    fun onWeeklyGoalChanged(value: String) {
        inputState.update { current ->
            current.copy(
                weeklyGoalInput = value,
                error = null
            )
        }
    }

    fun saveGoal(onSuccess: () -> Unit) {
        when (val result = validateWeeklyGoalUseCase(inputState.value.weeklyGoalInput)) {
            WeeklyGoalValidationResult.Error.INVALID_NUMBER -> {
                inputState.update { current ->
                    current.copy(error = GoalInputError.INVALID_NUMBER)
                }
            }

            WeeklyGoalValidationResult.Error.NON_POSITIVE -> {
                inputState.update { current ->
                    current.copy(error = GoalInputError.NON_POSITIVE)
                }
            }

            is WeeklyGoalValidationResult.Valid -> {
                viewModelScope.launch {
                    upsertRunningGoalUseCase(RunningGoal(weeklyGoalKm = result.weeklyGoalKm))
                    inputState.update { current ->
                        current.copy(error = null)
                    }
                    onSuccess()
                }
            }
        }
    }
}
