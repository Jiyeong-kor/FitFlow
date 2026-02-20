package com.jeong.fitflow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeong.fitflow.data.contract.RunningDatabaseContract
import kotlinx.coroutines.flow.Flow

@Dao
interface RunningRecordDao {

    @Query(RunningDatabaseContract.QUERY_GET_ALL_RECORDS)
    fun getAllRecords(): Flow<List<RunningRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RunningRecordEntity): Long

    @Query("SELECT COUNT(*) FROM ${RunningDatabaseContract.TABLE_RUNNING_RECORD}")
    suspend fun countAll(): Int

    @Query("SELECT COUNT(*) FROM ${RunningDatabaseContract.TABLE_RUNNING_RECORD} WHERE date = :dateMillis")
    suspend fun countByDate(dateMillis: Long): Int
}
