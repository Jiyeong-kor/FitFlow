package com.jeong.runninggoaltracker.feature.reminder.presentation

enum class ReminderViewMode {
    LIST,
    EDIT
}

data class ReminderUiState(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean,
    val days: Set<Int>
)

data class EditingReminderState(
    val id: Int?,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean,
    val days: Set<Int>,
    val isTimePickerVisible: Boolean = false
)

data class ReminderListUiState(
    val reminders: List<ReminderUiState> = emptyList(),
    val viewMode: ReminderViewMode = ReminderViewMode.LIST,
    val editingReminder: EditingReminderState? = null
)
