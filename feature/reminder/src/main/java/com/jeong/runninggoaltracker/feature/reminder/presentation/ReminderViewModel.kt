package com.jeong.runninggoaltracker.feature.reminder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.usecase.CreateDefaultReminderUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRemindersUseCase
import com.jeong.runninggoaltracker.domain.usecase.ToggleReminderDayUseCase
import com.jeong.runninggoaltracker.domain.util.RunningReminderValidator
import com.jeong.runninggoaltracker.feature.reminder.alarm.ReminderSchedulingInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    getRunningRemindersUseCase: GetRunningRemindersUseCase,
    createDefaultReminderUseCase: CreateDefaultReminderUseCase,
    toggleReminderDayUseCase: ToggleReminderDayUseCase,
    private val reminderSchedulingInteractor: ReminderSchedulingInteractor,
    private val reminderUiStateMapper: ReminderUiStateMapper,
    private val reminderUpdateHandler: ReminderUpdateHandler,
    private val reminderValidator: RunningReminderValidator
) : ViewModel() {

    private val stateHolder = ReminderStateHolder(
        scope = viewModelScope,
        getRunningRemindersUseCase = getRunningRemindersUseCase,
        createDefaultReminderUseCase = createDefaultReminderUseCase,
        toggleReminderDayUseCase = toggleReminderDayUseCase,
        reminderUiStateMapper = reminderUiStateMapper
    )

    val uiState: StateFlow<ReminderListUiState> = stateHolder.uiState

    fun onAddClick() {
        stateHolder.onAddClick()
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
        stateHolder.onReminderClick(id)
    }

    fun onCancelEdit() {
        stateHolder.onCancelEdit()
    }

    fun onSaveEdit() {
        val currentEditing = stateHolder.currentEditing() ?: return
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
        stateHolder.finishEdit()
    }

    fun onDeleteEdit() {
        val currentEditing = stateHolder.currentEditing() ?: return
        val reminderId = currentEditing.id
        if (reminderId == null) {
            onCancelEdit()
            return
        }
        viewModelScope.launch {
            reminderUpdateHandler.buildDeleteCommand(uiState.value.reminders, reminderId)
                .execute(reminderSchedulingInteractor)
        }
        stateHolder.finishEdit()
    }

    fun onToggleEditingEnabled(isEnabled: Boolean) {
        stateHolder.onToggleEditingEnabled(isEnabled)
    }

    fun onUpdateEditingTime(hour: Int, minute: Int) {
        stateHolder.onUpdateEditingTime(hour, minute)
    }

    fun onToggleEditingDay(day: Int) {
        stateHolder.onToggleEditingDay(day)
    }

    fun onOpenTimePicker() {
        stateHolder.onOpenTimePicker()
    }

    fun onDismissTimePicker() {
        stateHolder.onDismissTimePicker()
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
