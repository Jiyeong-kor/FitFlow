package com.jeong.runninggoaltracker.domain.util

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import javax.inject.Inject

class RunningReminderValidator @Inject constructor() {
    fun normalizeEnabledDays(reminder: RunningReminder): RunningReminder =
        if (reminder.isEnabled && reminder.days.isEmpty()) {
            reminder.copy(isEnabled = false)
        } else {
            reminder
        }
}
