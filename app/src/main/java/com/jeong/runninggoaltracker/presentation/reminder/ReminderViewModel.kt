package com.jeong.runninggoaltracker.presentation.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.repository.RunningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReminderUiState(
    val id: Int? = null,
    val hour: Int = 20,
    val minute: Int = 0,
    val enabled: Boolean = false,
    val days: Set<Int> = setOf()
)

data class ReminderListUiState(
    val reminders: List<ReminderUiState> = emptyList()
)

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: RunningRepository
) : ViewModel() {

    val uiState: StateFlow<ReminderListUiState> =
        repository.getAllReminders()
            .map { reminders ->
                ReminderListUiState(
                    reminders = reminders.map { reminder ->
                        ReminderUiState(
                            id = reminder.id,
                            hour = reminder.hour,
                            minute = reminder.minute,
                            enabled = reminder.enabled,
                            days = reminder.days
                        )
                    }.sortedBy { it.id }
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ReminderListUiState()
            )

    fun addReminder() {
        val newReminder = RunningReminder(
            hour = 8,
            minute = 0,
            enabled = false,
            days = emptySet()
        )
        save(newReminder)
    }

    fun deleteReminder(id: Int) {
        viewModelScope.launch {
            repository.deleteReminder(id)
        }
    }

    private fun updateReminder(id: Int, update: (ReminderUiState) -> ReminderUiState) {
        val currentReminder = uiState.value.reminders.find { it.id == id } ?: return
        val newReminderUiState = update(currentReminder)

        save(
            RunningReminder(
                id = newReminderUiState.id,
                hour = newReminderUiState.hour,
                minute = newReminderUiState.minute,
                enabled = newReminderUiState.enabled,
                days = newReminderUiState.days
            )
        )
    }

    fun updateEnabled(id: Int, enabled: Boolean) {
        updateReminder(id) { it.copy(enabled = enabled) }
    }

    fun updateTime(id: Int, hour: Int, minute: Int) {
        updateReminder(id) { it.copy(hour = hour, minute = minute) }
    }

    fun toggleDay(id: Int, day: Int) {
        updateReminder(id) { current ->
            val newDays = if (current.days.contains(day)) {
                current.days.minus(day)
            } else {
                current.days.plus(day)
            }
            current.copy(days = newDays)
        }
    }

    private fun save(reminder: RunningReminder) {
        viewModelScope.launch {
            repository.upsertReminder(reminder)
        }
    }
}
