package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.repository.RunningReminderRepository
import javax.inject.Inject

class UpsertRunningReminderUseCase @Inject constructor(
    private val repository: RunningReminderRepository
) {
    suspend operator fun invoke(reminder: RunningReminder) {
        repository.upsertReminder(reminder)
    }
}
