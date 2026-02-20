package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.repository.RunningReminderRepository
import javax.inject.Inject

class DeleteRunningReminderUseCase @Inject constructor(
    private val repository: RunningReminderRepository
) {
    suspend operator fun invoke(reminderId: Int) {
        repository.deleteReminder(reminderId)
    }
}
