package com.jeong.fitflow.feature.reminder.alarm

import com.jeong.fitflow.domain.model.RunningReminder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderSchedulerCoordinator @Inject constructor(
    private val reminderAlarmScheduler: ReminderAlarmScheduler
) : ReminderScheduler {

    override fun scheduleIfNeeded(reminder: RunningReminder) {
        if (!reminder.isEnabled || reminder.days.isEmpty()) {
            cancel(reminder)
            return
        }

        reminderAlarmScheduler.schedule(
            reminder.id,
            reminder.hour,
            reminder.minute,
            reminder.days
        )
    }

    override fun cancel(reminder: RunningReminder) {
        reminderAlarmScheduler.cancel(
            reminder.id,
            reminder.hour,
            reminder.minute,
            reminder.days
        )
    }
}
