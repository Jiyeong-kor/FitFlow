package com.jeong.fitflow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeong.fitflow.data.contract.RunningDatabaseContract
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutRecordDao {

    @Query(
        "SELECT * FROM ${RunningDatabaseContract.TABLE_WORKOUT_RECORD} " +
            "ORDER BY date DESC"
    )
    fun getAllRecords(): Flow<List<WorkoutRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: WorkoutRecordEntity)

    @Query("SELECT COUNT(*) FROM ${RunningDatabaseContract.TABLE_WORKOUT_RECORD}")
    suspend fun countAll(): Int
}
