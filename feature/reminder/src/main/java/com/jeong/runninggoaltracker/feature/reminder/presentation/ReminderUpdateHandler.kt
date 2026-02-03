package com.jeong.runninggoaltracker.feature.reminder.presentation

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.util.RunningReminderValidator
import com.jeong.runninggoaltracker.feature.reminder.alarm.ReminderSchedulingInteractor
import javax.inject.Inject

sealed interface ReminderUpdateCommand {
    suspend fun execute(interactor: ReminderSchedulingInteractor)
}

private data object ReminderNoOpCommand : ReminderUpdateCommand {
    override suspend fun execute(interactor: ReminderSchedulingInteractor) {
    }
}

private data class ReminderSaveCommand(
    val updatedReminder: RunningReminder,
    val previousReminder: RunningReminder
) : ReminderUpdateCommand {
    override suspend fun execute(interactor: ReminderSchedulingInteractor) {
        interactor.saveReminder(
            updatedReminder = updatedReminder,
            previousReminder = previousReminder
        )
    }
}

private data class ReminderDeleteCommand(
    val reminder: RunningReminder
) : ReminderUpdateCommand {
    override suspend fun execute(interactor: ReminderSchedulingInteractor) {
        interactor.deleteReminder(reminder)
    }
}

class ReminderUpdateHandler @Inject constructor(
    private val reminderUiStateMapper: ReminderUiStateMapper,
    private val reminderValidator: RunningReminderValidator
) {

    fun buildUpdateCommand(
        reminders: List<ReminderUiState>,
        id: Int,
        update: (RunningReminder) -> RunningReminder
    ): ReminderUpdateCommand {
        val currentReminderUiState = reminders.find { it.id == id } ?: return ReminderNoOpCommand
        val currentRunningReminder = reminderUiStateMapper.toDomain(currentReminderUiState)
        val updatedReminder = reminderValidator.normalizeEnabledDays(update(currentRunningReminder))
        if (updatedReminder == currentRunningReminder) {
            return ReminderNoOpCommand
        }
        return ReminderSaveCommand(
            updatedReminder = updatedReminder,
            previousReminder = currentRunningReminder
        )
    }

    fun buildDeleteCommand(reminders: List<ReminderUiState>, id: Int): ReminderUpdateCommand {
        val currentReminderUiState = reminders.find { it.id == id } ?: return ReminderNoOpCommand
        return ReminderDeleteCommand(reminderUiStateMapper.toDomain(currentReminderUiState))
    }
}
