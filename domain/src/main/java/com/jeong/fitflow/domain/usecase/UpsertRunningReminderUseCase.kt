package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.RunningReminder
import com.jeong.fitflow.domain.repository.RunningReminderRepository
import javax.inject.Inject

class UpsertRunningReminderUseCase @Inject constructor(
    private val repository: RunningReminderRepository
) {
    suspend operator fun invoke(reminder: RunningReminder) {
        repository.upsertReminder(reminder)
    }
}
