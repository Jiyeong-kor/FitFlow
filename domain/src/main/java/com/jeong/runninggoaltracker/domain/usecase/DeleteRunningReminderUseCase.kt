package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.repository.RunningReminderRepository
import javax.inject.Inject

class DeleteRunningReminderUseCase @Inject constructor(
    private val repository: RunningReminderRepository
) {
    suspend operator fun invoke(reminderId: Int) {
        repository.deleteReminder(reminderId)
    }
}
