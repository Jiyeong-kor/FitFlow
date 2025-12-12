package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import java.time.DayOfWeek

class ToggleReminderDayUseCase {
    operator fun invoke(reminder: RunningReminder, day: DayOfWeek): RunningReminder {
        val newDays = if (reminder.days.contains(day)) {
            reminder.days.minus(day)
        } else {
            reminder.days.plus(day)
        }

        return reminder.copy(days = newDays)
    }
}
