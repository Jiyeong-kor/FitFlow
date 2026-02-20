package com.jeong.fitflow.domain.util

import com.jeong.fitflow.domain.model.RunningReminder
import javax.inject.Inject

class RunningReminderValidator @Inject constructor() {
    fun normalizeEnabledDays(reminder: RunningReminder): RunningReminder =
        if (shouldDisableEmptyDays(reminder)) {
            reminder.copy(isEnabled = false)
        } else {
            reminder
        }

    private fun shouldDisableEmptyDays(reminder: RunningReminder): Boolean =
        reminder.isEnabled && reminder.days.isEmpty()
}
