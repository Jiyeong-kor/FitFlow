package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import java.time.DayOfWeek

class ToggleReminderDayUseCase {
    operator fun invoke(reminder: RunningReminder, day: Int): RunningReminder {
        val dayOfWeek = runCatching { DayOfWeek.of(day) }
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
