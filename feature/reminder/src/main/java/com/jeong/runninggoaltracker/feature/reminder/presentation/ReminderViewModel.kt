package com.jeong.runninggoaltracker.feature.reminder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.usecase.CreateDefaultReminderUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRemindersUseCase
import com.jeong.runninggoaltracker.domain.usecase.ToggleReminderDayUseCase
import com.jeong.runninggoaltracker.domain.util.RunningReminderValidator
import com.jeong.runninggoaltracker.feature.reminder.alarm.ReminderSchedulingInteractor
import com.jeong.runninggoaltracker.feature.reminder.contract.REMINDER_STATE_TIMEOUT_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReminderUiState(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean,
    val days: Set<Int>
)

data class ReminderListUiState(
    val reminders: List<ReminderUiState> = emptyList()
)

@HiltViewModel
class ReminderViewModel @Inject constructor(
    getRunningRemindersUseCase: GetRunningRemindersUseCase,
    private val createDefaultReminderUseCase: CreateDefaultReminderUseCase,
    private val toggleReminderDayUseCase: ToggleReminderDayUseCase,
    private val reminderSchedulingInteractor: ReminderSchedulingInteractor,
    private val reminderUiStateMapper: ReminderUiStateMapper,
    private val reminderValidator: RunningReminderValidator
) : ViewModel() {

    val uiState: StateFlow<ReminderListUiState> =
        getRunningRemindersUseCase()
            .map { reminders -> reminderUiStateMapper.toListUiState(reminders) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(REMINDER_STATE_TIMEOUT_MS),
                initialValue = ReminderListUiState()
            )

    fun addReminder() {
        viewModelScope.launch {
            val newReminder = createDefaultReminderUseCase()
            reminderSchedulingInteractor.saveReminder(
                updatedReminder = newReminder,
                previousReminder = null
            )
        }
    }

    fun deleteReminder(id: Int) {
        val currentReminder = uiState.value.reminders.find { it.id == id }?.let {
            reminderUiStateMapper.toDomain(it)
        } ?: return

        viewModelScope.launch {
            reminderSchedulingInteractor.deleteReminder(currentReminder)
        }
    }

    fun updateEnabled(id: Int, enabled: Boolean) =
        updateReminder(id) { reminder -> reminder.copy(enabled = enabled) }

    fun updateTime(id: Int, hour: Int, minute: Int) =
        updateReminder(id) { it.copy(hour = hour, minute = minute) }

    fun toggleDay(id: Int, day: Int) =
        updateReminder(id) { current -> toggleReminderDayUseCase(current, day) }

    private fun updateReminder(
        id: Int,
        update: (RunningReminder) -> RunningReminder
    ) {
        val currentReminderUiState = uiState.value.reminders.find { it.id == id } ?: return
        val currentRunningReminder = reminderUiStateMapper.toDomain(currentReminderUiState)

        val updatedReminder = reminderValidator.normalizeEnabledDays(update(currentRunningReminder))
        if (updatedReminder == currentRunningReminder) return

        viewModelScope.launch {
            reminderSchedulingInteractor.saveReminder(
                updatedReminder = updatedReminder,
                previousReminder = currentRunningReminder
            )
        }
    }
}
