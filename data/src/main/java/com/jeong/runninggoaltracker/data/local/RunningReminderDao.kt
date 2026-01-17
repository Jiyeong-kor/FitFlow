package com.jeong.runninggoaltracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeong.runninggoaltracker.data.contract.RunningDatabaseContract
import kotlinx.coroutines.flow.Flow

@Dao
interface RunningReminderDao {

    @Query(RunningDatabaseContract.QUERY_GET_ALL_REMINDERS)
    fun getAllReminders(): Flow<List<RunningReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReminder(reminder: RunningReminderEntity)

    @Query(RunningDatabaseContract.QUERY_DELETE_REMINDER)
    suspend fun deleteReminder(reminderId: Int)
}
