package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.RunningReminder
import javax.inject.Inject

class CreateDefaultReminderUseCase @Inject constructor() {
    operator fun invoke(): RunningReminder = RunningReminder(
        id = null,
        hour = 8,
        minute = 0,
        isEnabled = false,
        days = emptySet()
    )
}
