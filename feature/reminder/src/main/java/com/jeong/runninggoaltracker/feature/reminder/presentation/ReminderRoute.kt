package com.jeong.runninggoaltracker.feature.reminder.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ReminderRoute(
    viewModel: ReminderViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ReminderScreen(
        state = state,
        onAddReminder = viewModel::addReminder,
        onDeleteReminder = viewModel::deleteReminder,
        onToggleReminder = viewModel::updateEnabled,
        onUpdateTime = viewModel::updateTime,
        onToggleDay = viewModel::toggleDay,
        onOpenTimePicker = viewModel::openTimePicker,
        onDismissTimePicker = viewModel::dismissTimePicker
    )
}
