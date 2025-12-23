package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.model.time.AppDayOfWeek
import javax.inject.Inject

class ToggleReminderDayUseCase @Inject constructor() {
    operator fun invoke(reminder: RunningReminder, day: Int): RunningReminder {
        val dayOfWeek = runCatching { AppDayOfWeek.of(day) }
            .getOrNull()
            ?: return reminder

        val newDays = if (reminder.days.contains(dayOfWeek)) {
            reminder.days.minus(dayOfWeek)
        } else {
            reminder.days.plus(dayOfWeek)
        }

        return reminder.copy(days = newDays)
    }
}
