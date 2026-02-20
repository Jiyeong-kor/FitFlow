package com.jeong.fitflow.feature.reminder.presentation

import com.jeong.fitflow.domain.model.RunningReminder
import javax.inject.Inject

class ReminderUiStateMapper @Inject constructor() {

    fun toListUiState(
        reminders: List<RunningReminder>,
        viewMode: ReminderViewMode,
        editingReminder: EditingReminderState?
    ): ReminderListUiState =
        ReminderListUiState(
            reminders = reminders.mapNotNull { toUiStateOrNull(it) },
            viewMode = viewMode,
            editingReminder = editingReminder
        )

    fun toDomain(uiState: ReminderUiState): RunningReminder =
        RunningReminder(
            id = uiState.id,
            hour = uiState.hour,
            minute = uiState.minute,
            isEnabled = uiState.isEnabled,
            days = uiState.days
        )

    fun toDomain(editingState: EditingReminderState): RunningReminder =
        RunningReminder(
            id = editingState.id,
            hour = editingState.hour,
            minute = editingState.minute,
            isEnabled = editingState.isEnabled,
            days = editingState.days
        )

    fun toEditingState(uiState: ReminderUiState): EditingReminderState =
        EditingReminderState(
            id = uiState.id,
            hour = uiState.hour,
            minute = uiState.minute,
            isEnabled = uiState.isEnabled,
            days = uiState.days
        )

    fun toEditingState(reminder: RunningReminder): EditingReminderState =
        EditingReminderState(
            id = reminder.id,
            hour = reminder.hour,
            minute = reminder.minute,
            isEnabled = reminder.isEnabled,
            days = reminder.days
        )

    private fun toUiStateOrNull(reminder: RunningReminder): ReminderUiState? {
        val reminderId = reminder.id ?: return null
        return ReminderUiState(
            id = reminderId,
            hour = reminder.hour,
            minute = reminder.minute,
            isEnabled = reminder.isEnabled,
            days = reminder.days
        )
    }
}
