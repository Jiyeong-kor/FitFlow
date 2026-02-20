package com.jeong.fitflow.domain.repository

import com.jeong.fitflow.domain.model.RunningReminder
import kotlinx.coroutines.flow.Flow

interface RunningReminderRepository {
    fun getAllReminders(): Flow<List<RunningReminder>>
    suspend fun upsertReminder(reminder: RunningReminder)
    suspend fun deleteReminder(reminderId: Int)
}
