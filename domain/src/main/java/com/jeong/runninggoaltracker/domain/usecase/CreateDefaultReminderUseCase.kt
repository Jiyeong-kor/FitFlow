package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import javax.inject.Inject

class CreateDefaultReminderUseCase @Inject constructor() {
    operator fun invoke(): RunningReminder = RunningReminder(
        id = null,
        hour = 8,
        minute = 0,
        enabled = false,
        days = emptySet()
    )
}
