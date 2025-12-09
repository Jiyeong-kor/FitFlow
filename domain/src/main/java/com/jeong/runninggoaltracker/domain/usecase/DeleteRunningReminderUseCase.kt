package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.repository.RunningRepository

class DeleteRunningReminderUseCase(
    private val repository: RunningRepository
) {
    suspend operator fun invoke(reminderId: Int) {
        repository.deleteReminder(reminderId)
    }
}
