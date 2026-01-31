package com.jeong.runninggoaltracker.domain.util

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import javax.inject.Inject

class RunningReminderValidator @Inject constructor() {
    fun normalizeEnabledDays(reminder: RunningReminder): RunningReminder =
        if (reminder.enabled && reminder.days.isEmpty()) {
            reminder.copy(enabled = false)
        } else {
            reminder
        }
}
