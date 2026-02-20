package com.jeong.fitflow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeong.fitflow.data.contract.RunningDatabaseContract
import kotlinx.coroutines.flow.Flow

@Dao
interface RunningReminderDao {

    @Query(RunningDatabaseContract.QUERY_GET_ALL_REMINDERS)
    fun getAllReminders(): Flow<List<RunningReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReminder(reminder: RunningReminderEntity)

    @Query("SELECT COALESCE(MAX(id), 0) + 1 FROM ${RunningDatabaseContract.TABLE_RUNNING_REMINDER}")
    suspend fun getNextReminderId(): Int

    @Query(RunningDatabaseContract.QUERY_DELETE_REMINDER)
    suspend fun deleteReminder(reminderId: Int)

    @Query("SELECT COUNT(*) FROM ${RunningDatabaseContract.TABLE_RUNNING_REMINDER}")
    suspend fun countAll(): Int
}
