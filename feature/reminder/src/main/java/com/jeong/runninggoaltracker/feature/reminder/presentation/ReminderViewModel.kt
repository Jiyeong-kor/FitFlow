package com.jeong.runninggoaltracker.feature.reminder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.usecase.CreateDefaultReminderUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRemindersUseCase
import com.jeong.runninggoaltracker.domain.usecase.ToggleReminderDayUseCase
import com.jeong.runninggoaltracker.feature.reminder.alarm.ReminderSchedulingInteractor
import com.jeong.runninggoaltracker.feature.reminder.contract.REMINDER_STATE_TIMEOUT_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    val reminders: List<ReminderUiState> = emptyList(),
    val activeTimePickerId: Int? = null
)

@HiltViewModel
class ReminderViewModel @Inject constructor(
    getRunningRemindersUseCase: GetRunningRemindersUseCase,
    private val createDefaultReminderUseCase: CreateDefaultReminderUseCase,
    private val toggleReminderDayUseCase: ToggleReminderDayUseCase,
    private val reminderSchedulingInteractor: ReminderSchedulingInteractor,
    private val reminderUiStateMapper: ReminderUiStateMapper,
    private val reminderUpdateHandler: ReminderUpdateHandler
) : ViewModel() {

    private val activeTimePickerId = kotlinx.coroutines.flow.MutableStateFlow<Int?>(null)
    val uiState: StateFlow<ReminderListUiState> =
        getRunningRemindersUseCase()
            .combine(activeTimePickerId) { reminders, pickerId ->
                reminderUiStateMapper.toListUiState(reminders, pickerId)
            }
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
        viewModelScope.launch {
            reminderUpdateHandler.buildDeleteCommand(uiState.value.reminders, id)
                .execute(reminderSchedulingInteractor)
        }
    }

    fun updateEnabled(id: Int, enabled: Boolean) {
        updateReminder(id) { reminder -> reminder.copy(enabled = enabled) }
    }

    fun updateTime(id: Int, hour: Int, minute: Int) {
        updateReminder(id) { it.copy(hour = hour, minute = minute) }
    }

    fun toggleDay(id: Int, day: Int) {
        updateReminder(id) { current -> toggleReminderDayUseCase(current, day) }
    }

    fun openTimePicker(id: Int) {
        activeTimePickerId.value = id
    }

    fun dismissTimePicker() {
        activeTimePickerId.value = null
    }

    private fun updateReminder(
        id: Int,
        update: (RunningReminder) -> RunningReminder
    ) {
        viewModelScope.launch {
            reminderUpdateHandler
                .buildUpdateCommand(
                    reminders = uiState.value.reminders,
                    id = id,
                    update = update
                )
                .execute(reminderSchedulingInteractor)
        }
    }
}
