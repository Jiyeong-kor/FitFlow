package com.jeong.runninggoaltracker.feature.reminder.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.usecase.CreateDefaultReminderUseCase
import com.jeong.runninggoaltracker.domain.usecase.DeleteRunningReminderUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRemindersUseCase
import com.jeong.runninggoaltracker.domain.usecase.ToggleReminderDayUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpsertRunningReminderUseCase
import com.jeong.runninggoaltracker.feature.reminder.alarm.ReminderSchedulerCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

data class ReminderUiState(
    val id: Int? = null,
    val hour: Int = 20,
    val minute: Int = 0,
    val enabled: Boolean = false,
    val days: Set<Int> = emptySet()
)

data class ReminderListUiState(
    val reminders: List<ReminderUiState> = emptyList()
)

@HiltViewModel
class ReminderViewModel @Inject constructor(
    getRunningRemindersUseCase: GetRunningRemindersUseCase,
    private val deleteRunningReminderUseCase: DeleteRunningReminderUseCase,
    private val upsertRunningReminderUseCase: UpsertRunningReminderUseCase,
    private val createDefaultReminderUseCase: CreateDefaultReminderUseCase,
    private val toggleReminderDayUseCase: ToggleReminderDayUseCase,
    private val schedulerCoordinator: ReminderSchedulerCoordinator
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<ReminderListUiState> =
        getRunningRemindersUseCase()
            .map { reminders -> ReminderListUiState(reminders.map { it.toUiState() }) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ReminderListUiState()
            )

    fun addReminder() {
        viewModelScope.launch {
            val newReminder = createDefaultReminderUseCase()
            upsertRunningReminderUseCase(newReminder)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteReminder(id: Int) {
        val currentReminder = uiState.value.reminders.find { it.id == id }?.toDomainOrNull()
        if (currentReminder != null) {
            schedulerCoordinator.cancel(currentReminder)
        }
        viewModelScope.launch { deleteRunningReminderUseCase(id) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateEnabled(id: Int, enabled: Boolean) {
        updateAndPersistReminder(id) { reminder ->
            if (enabled && reminder.days.isEmpty()) {
                reminder
            } else {
                reminder.copy(enabled = enabled)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTime(id: Int, hour: Int, minute: Int) {
        updateAndPersistReminder(id) { it.copy(hour = hour, minute = minute) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toggleDay(id: Int, day: Int) {
        updateAndPersistReminder(id) { current ->
            val toggled = toggleReminderDayUseCase(current, day)
            if (toggled.enabled && toggled.days.isEmpty()) {
                toggled.copy(enabled = false)
            } else {
                toggled
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateAndPersistReminder(
        id: Int,
        update: (RunningReminder) -> RunningReminder
    ) {
        val currentReminderUiState = uiState.value.reminders.find { it.id == id } ?: return

        val currentRunningReminder = currentReminderUiState.toDomainOrNull() ?: return

        val newRunningReminder = update(currentRunningReminder)

        viewModelScope.launch {
            upsertRunningReminderUseCase(newRunningReminder)
            rescheduleReminder(currentRunningReminder, newRunningReminder)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun rescheduleReminder(
        previous: RunningReminder,
        updated: RunningReminder
    ) {
        schedulerCoordinator.cancel(previous)
        schedulerCoordinator.scheduleIfNeeded(updated)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun RunningReminder.toUiState(): ReminderUiState =
    ReminderUiState(
        id = id,
        hour = hour,
        minute = minute,
        enabled = enabled,
        days = days.map { it.value }.toSet()
    )

@RequiresApi(Build.VERSION_CODES.O)
private fun ReminderUiState.toDomain(): RunningReminder =
    RunningReminder(
        id = id,
        hour = hour,
        minute = minute,
        enabled = enabled,
        days = days.map { DayOfWeek.of(it) }.toSet()
    )

@RequiresApi(Build.VERSION_CODES.O)
private fun ReminderUiState.toDomainOrNull(): RunningReminder? =
    runCatching { toDomain() }.getOrNull()
