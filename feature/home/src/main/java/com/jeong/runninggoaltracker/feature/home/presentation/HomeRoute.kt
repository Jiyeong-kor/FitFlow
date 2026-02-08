package com.jeong.runninggoaltracker.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(
    onNavigateToRecord: () -> Unit,
    onNavigateToGoal: () -> Unit,
    onNavigateToReminder: () -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onPeriodSelected = viewModel::onPeriodSelected,
        onNavigatePreviousPeriod = viewModel::onNavigatePreviousPeriod,
        onNavigateNextPeriod = viewModel::onNavigateNextPeriod,
        onDateSelected = viewModel::onDateSelected,
        onCalendarOpen = viewModel::onCalendarOpen,
        onCalendarDismiss = viewModel::onCalendarDismiss,
        onCalendarPreviousMonth = viewModel::onPreviousCalendarMonth,
        onCalendarNextMonth = viewModel::onNextCalendarMonth,
        onRecordClick = onNavigateToRecord,
        onGoalClick = onNavigateToGoal,
        onReminderClick = onNavigateToReminder
    )
}
