package com.jeong.fitflow.feature.reminder.alarm

import com.jeong.fitflow.domain.model.RunningReminder

interface ReminderScheduler {
    fun scheduleIfNeeded(reminder: RunningReminder)
    fun cancel(reminder: RunningReminder)
}
