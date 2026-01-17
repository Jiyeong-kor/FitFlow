package com.jeong.runninggoaltracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeong.runninggoaltracker.data.contract.RunningDatabaseContract
import kotlinx.coroutines.flow.Flow

@Dao
interface RunningRecordDao {

    @Query(RunningDatabaseContract.QUERY_GET_ALL_RECORDS)
    fun getAllRecords(): Flow<List<RunningRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RunningRecordEntity)
}
