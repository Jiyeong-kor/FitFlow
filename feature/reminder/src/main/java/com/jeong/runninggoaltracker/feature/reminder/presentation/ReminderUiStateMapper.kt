package com.jeong.runninggoaltracker.feature.reminder.presentation

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import javax.inject.Inject

class ReminderUiStateMapper @Inject constructor() {

    fun toListUiState(
        reminders: List<RunningReminder>,
        activeTimePickerId: Int?
    ): ReminderListUiState =
        ReminderListUiState(
            reminders = reminders.mapNotNull { toUiStateOrNull(it) },
            activeTimePickerId = activeTimePickerId
        )

    fun toDomain(uiState: ReminderUiState): RunningReminder =
        RunningReminder(
            id = uiState.id,
            hour = uiState.hour,
            minute = uiState.minute,
            isEnabled = uiState.isEnabled,
            days = uiState.days
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
