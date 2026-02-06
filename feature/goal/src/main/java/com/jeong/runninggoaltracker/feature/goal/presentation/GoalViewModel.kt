package com.jeong.runninggoaltracker.feature.goal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.usecase.GetRunningGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GoalViewModel @Inject constructor(
    getRunningGoalUseCase: GetRunningGoalUseCase,
    goalSaveHandler: GoalSaveHandler
) : ViewModel() {

    private val stateHolder = GoalStateHolder(
        scope = viewModelScope,
        getRunningGoalUseCase = getRunningGoalUseCase,
        goalSaveHandler = goalSaveHandler
    )

    val uiState: StateFlow<GoalUiState> = stateHolder.uiState

    fun onWeeklyGoalChanged(value: Double) = stateHolder.onWeeklyGoalChanged(value)

    fun saveGoal(onSuccess: () -> Unit) =
        viewModelScope.launch {
            stateHolder.saveGoal(onSuccess)
        }
}
