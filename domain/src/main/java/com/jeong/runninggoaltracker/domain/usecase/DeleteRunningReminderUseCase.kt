package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.repository.RunningReminderRepository

class DeleteRunningReminderUseCase(
    private val repository: RunningReminderRepository
) {
    suspend operator fun invoke(reminderId: Int) {
        repository.deleteReminder(reminderId)
    }
}
