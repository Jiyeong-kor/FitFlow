package com.jeong.runninggoaltracker.feature.goal.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GoalRoute(
    onBack: () -> Unit,
    viewModel: GoalViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GoalScreen(
        state = state,
        onGoalChange = viewModel::onWeeklyGoalChanged,
        onSave = { viewModel.saveGoal(onBack) }
    )
}
