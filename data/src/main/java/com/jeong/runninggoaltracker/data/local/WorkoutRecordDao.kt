package com.jeong.runninggoaltracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface WorkoutRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: WorkoutRecordEntity)
}
