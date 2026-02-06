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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    getRunningRemindersUseCase: GetRunningRemindersUseCase,
    private val createDefaultReminderUseCase: CreateDefaultReminderUseCase,
    private val toggleReminderDayUseCase: ToggleReminderDayUseCase,
    private val reminderSchedulingInteractor: ReminderSchedulingInteractor,
    private val reminderUiStateMapper: ReminderUiStateMapper,
    private val reminderUpdateHandler: ReminderUpdateHandler,
    private val reminderValidator: RunningReminderValidator
) : ViewModel() {

    private val viewMode = MutableStateFlow(ReminderViewMode.LIST)
    private val editingReminder = MutableStateFlow<EditingReminderState?>(null)
    val uiState: StateFlow<ReminderListUiState> =
        getRunningRemindersUseCase()
            .combine(viewMode) { reminders, mode ->
                reminders to mode
            }
            .combine(editingReminder) { (reminders, mode), editing ->
                reminderUiStateMapper.toListUiState(reminders, mode, editing)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(REMINDER_STATE_TIMEOUT_MS),
                initialValue = ReminderListUiState()
            )

    fun onAddClick() {
        val defaultReminder = createDefaultReminderUseCase()
        viewMode.value = ReminderViewMode.EDIT
        editingReminder.value = reminderUiStateMapper.toEditingState(defaultReminder)
    }

    fun deleteReminder(id: Int) {
        viewModelScope.launch {
            reminderUpdateHandler.buildDeleteCommand(uiState.value.reminders, id)
                .execute(reminderSchedulingInteractor)
        }
    }

    fun updateEnabled(id: Int, isEnabled: Boolean) {
        updateReminder(id) { reminder -> reminder.copy(isEnabled = isEnabled) }
    }

    fun onReminderClick(id: Int) {
        val reminder = uiState.value.reminders.firstOrNull { it.id == id } ?: return
        viewMode.value = ReminderViewMode.EDIT
        editingReminder.value = reminderUiStateMapper.toEditingState(reminder)
    }

    fun onCancelEdit() {
        viewMode.value = ReminderViewMode.LIST
        editingReminder.value = null
    }

    fun onSaveEdit() {
        val currentEditing = editingReminder.value ?: return
        viewModelScope.launch {
            val normalizedReminder = reminderValidator.normalizeEnabledDays(
                reminderUiStateMapper.toDomain(currentEditing)
            )
            val reminderId = currentEditing.id
            if (reminderId == null) {
                reminderSchedulingInteractor.saveReminder(
                    updatedReminder = normalizedReminder,
                    previousReminder = null
                )
            } else {
                reminderUpdateHandler
                    .buildUpdateCommand(
                        reminders = uiState.value.reminders,
                        id = reminderId,
                        update = { normalizedReminder }
                    )
                    .execute(reminderSchedulingInteractor)
            }
        }
        viewMode.value = ReminderViewMode.LIST
        editingReminder.value = null
    }

    fun onDeleteEdit() {
        val currentEditing = editingReminder.value ?: return
        val reminderId = currentEditing.id
        if (reminderId == null) {
            onCancelEdit()
            return
        }
        viewModelScope.launch {
            reminderUpdateHandler.buildDeleteCommand(uiState.value.reminders, reminderId)
                .execute(reminderSchedulingInteractor)
        }
        viewMode.value = ReminderViewMode.LIST
        editingReminder.value = null
    }

    fun onToggleEditingEnabled(isEnabled: Boolean) {
        editingReminder.update { current ->
            current?.copy(isEnabled = isEnabled)
        }
    }

    fun onUpdateEditingTime(hour: Int, minute: Int) {
        editingReminder.update { current ->
            current?.copy(hour = hour, minute = minute)
        }
    }

    fun onToggleEditingDay(day: Int) {
        editingReminder.update { current ->
            current?.let {
                val updatedReminder = toggleReminderDayUseCase(
                    reminderUiStateMapper.toDomain(it),
                    day
                )
                it.copy(days = updatedReminder.days)
            }
        }
    }

    fun onOpenTimePicker() {
        editingReminder.update { current ->
            current?.copy(isTimePickerVisible = true)
        }
    }

    fun onDismissTimePicker() {
        editingReminder.update { current ->
            current?.copy(isTimePickerVisible = false)
        }
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
