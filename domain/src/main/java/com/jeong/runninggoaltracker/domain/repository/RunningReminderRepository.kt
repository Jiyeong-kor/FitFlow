package com.jeong.runninggoaltracker.domain.repository

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import kotlinx.coroutines.flow.Flow

interface RunningReminderRepository {
    fun getAllReminders(): Flow<List<RunningReminder>>
    suspend fun upsertReminder(reminder: RunningReminder)
    suspend fun deleteReminder(reminderId: Int)
}
