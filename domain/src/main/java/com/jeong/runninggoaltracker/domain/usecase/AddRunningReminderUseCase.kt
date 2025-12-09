package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.repository.RunningRepository

class AddRunningReminderUseCase(
    private val repository: RunningRepository
) {
    suspend operator fun invoke() {
        val newReminder = RunningReminder(
            hour = 8,
            minute = 0,
            enabled = false,
            days = emptySet()
        )
        repository.upsertReminder(newReminder)
    }
}
