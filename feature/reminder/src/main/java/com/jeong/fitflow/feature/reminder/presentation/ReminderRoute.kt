package com.jeong.fitflow.feature.reminder.presentation

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
        onAddClick = viewModel::onAddClick,
        onReminderClick = viewModel::onReminderClick,
        onDeleteReminder = viewModel::deleteReminder,
        onToggleReminder = viewModel::updateEnabled,
        onCancelEdit = viewModel::onCancelEdit,
        onSaveEdit = viewModel::onSaveEdit,
        onDeleteEdit = viewModel::onDeleteEdit,
        onToggleEditingEnabled = viewModel::onToggleEditingEnabled,
        onUpdateEditingTime = viewModel::onUpdateEditingTime,
        onToggleEditingDay = viewModel::onToggleEditingDay,
        onOpenTimePicker = viewModel::onOpenTimePicker,
        onDismissTimePicker = viewModel::onDismissTimePicker
    )
}
