package com.jeong.runninggoaltracker.feature.reminder.alarm

import android.os.Build
import androidx.annotation.RequiresApi
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderSchedulerCoordinator @Inject constructor(
    private val reminderAlarmScheduler: ReminderAlarmScheduler
) : ReminderScheduler {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun scheduleIfNeeded(reminder: RunningReminder) {
        if (!reminder.enabled || reminder.days.isEmpty()) return

        reminderAlarmScheduler.schedule(
            reminder.id,
            reminder.hour,
            reminder.minute,
            reminder.days.map { it.value }.toSet()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun cancel(reminder: RunningReminder) {
        reminderAlarmScheduler.cancel(
            reminder.id,
            reminder.hour,
            reminder.minute,
            reminder.days.map { it.value }.toSet()
        )
    }
}
