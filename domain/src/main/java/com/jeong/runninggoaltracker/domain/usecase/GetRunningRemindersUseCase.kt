package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.repository.RunningReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRunningRemindersUseCase(
    private val repository: RunningReminderRepository
) {
    operator fun invoke(): Flow<List<RunningReminder>> = repository.getAllReminders()
        .map { list ->
            list.sortedBy { it.id }
        }
}
