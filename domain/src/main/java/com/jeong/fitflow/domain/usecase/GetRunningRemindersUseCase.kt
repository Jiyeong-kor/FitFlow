package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.RunningReminder
import com.jeong.fitflow.domain.repository.RunningReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRunningRemindersUseCase @Inject constructor(
    private val repository: RunningReminderRepository
) {
    operator fun invoke(): Flow<List<RunningReminder>> = repository.getAllReminders()
        .map { reminders ->
            reminders.sortedBy { it.id ?: Int.MAX_VALUE }
        }
}
