package com.jeong.runninggoaltracker.feature.reminder.alarm

import com.jeong.runninggoaltracker.domain.model.RunningReminder

interface ReminderScheduler {
    fun scheduleIfNeeded(reminder: RunningReminder)
    fun cancel(reminder: RunningReminder)
}
