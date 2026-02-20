package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.RunningReminder
import javax.inject.Inject

class ToggleReminderDayUseCase @Inject constructor() {
    operator fun invoke(reminder: RunningReminder, day: Int): RunningReminder =
        reminder.toggleDay(day)
}
