package com.jeong.fitflow.feature.reminder.presentation

import com.jeong.fitflow.domain.usecase.CreateDefaultReminderUseCase
import com.jeong.fitflow.domain.usecase.GetRunningRemindersUseCase
import com.jeong.fitflow.domain.usecase.ToggleReminderDayUseCase
import com.jeong.fitflow.feature.reminder.contract.REMINDER_STATE_TIMEOUT_MS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ReminderStateHolder(
    scope: CoroutineScope,
    getRunningRemindersUseCase: GetRunningRemindersUseCase,
    private val createDefaultReminderUseCase: CreateDefaultReminderUseCase,
    private val toggleReminderDayUseCase: ToggleReminderDayUseCase,
    private val reminderUiStateMapper: ReminderUiStateMapper
) {

    private val viewMode = MutableStateFlow(ReminderViewMode.LIST)
    private val editingReminder = MutableStateFlow<EditingReminderState?>(null)
    private val remindersFlow = getRunningRemindersUseCase()

    val uiState: StateFlow<ReminderListUiState> =
        remindersFlow
            .combine(viewMode) { reminders, mode ->
                reminders to mode
            }
            .combine(editingReminder) { (reminders, mode), editing ->
                reminderUiStateMapper.toListUiState(reminders, mode, editing)
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(REMINDER_STATE_TIMEOUT_MS),
                initialValue = ReminderListUiState()
            )

    fun onAddClick() {
        val defaultReminder = createDefaultReminderUseCase()
        viewMode.value = ReminderViewMode.EDIT
        editingReminder.value = reminderUiStateMapper.toEditingState(defaultReminder)
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

    fun finishEdit() {
        viewMode.value = ReminderViewMode.LIST
        editingReminder.value = null
    }

    fun currentEditing(): EditingReminderState? = editingReminder.value
}
