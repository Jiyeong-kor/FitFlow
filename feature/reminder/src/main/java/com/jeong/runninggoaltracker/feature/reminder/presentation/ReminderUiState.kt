package com.jeong.runninggoaltracker.feature.reminder.presentation


data class ReminderUiState(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean,
    val days: Set<Int>
)

data class ReminderListUiState(
    val reminders: List<ReminderUiState> = emptyList(),
    val activeTimePickerId: Int? = null
)
