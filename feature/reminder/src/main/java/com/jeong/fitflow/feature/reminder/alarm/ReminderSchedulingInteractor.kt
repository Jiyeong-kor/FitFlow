package com.jeong.fitflow.feature.reminder.alarm

import com.jeong.fitflow.domain.model.RunningReminder
import com.jeong.fitflow.domain.usecase.DeleteRunningReminderUseCase
import com.jeong.fitflow.domain.usecase.UpsertRunningReminderUseCase
import javax.inject.Inject

class ReminderSchedulingInteractor @Inject constructor(
    private val upsertRunningReminderUseCase: UpsertRunningReminderUseCase,
    private val deleteRunningReminderUseCase: DeleteRunningReminderUseCase,
    private val reminderScheduler: ReminderScheduler,
) {

    suspend fun saveReminder(
        updatedReminder: RunningReminder,
        previousReminder: RunningReminder?,
    ) {
        upsertRunningReminderUseCase(updatedReminder)
        reschedule(
            previousReminder = previousReminder,
            updatedReminder = updatedReminder,
        )
    }

    suspend fun deleteReminder(reminder: RunningReminder) {
        val id = reminder.id ?: return
        reminderScheduler.cancel(reminder)
        deleteRunningReminderUseCase(id)
    }

    private fun reschedule(
        previousReminder: RunningReminder?,
        updatedReminder: RunningReminder,
    ) {
        if (previousReminder == updatedReminder) return

        previousReminder?.let(reminderScheduler::cancel)

        if (updatedReminder.shouldSchedule) {
            reminderScheduler.scheduleIfNeeded(updatedReminder)
        }
    }

    private val RunningReminder.shouldSchedule: Boolean
        get() = isEnabled && days.isNotEmpty()
}
