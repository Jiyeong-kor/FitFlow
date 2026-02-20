package com.jeong.fitflow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeong.fitflow.data.contract.RunningDatabaseContract
import kotlinx.coroutines.flow.Flow

@Dao
interface RunningGoalDao {

    @Query(RunningDatabaseContract.QUERY_GET_GOAL)
    fun getGoal(): Flow<RunningGoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGoal(goal: RunningGoalEntity)

    @Query("SELECT COUNT(*) FROM ${RunningDatabaseContract.TABLE_RUNNING_GOAL}")
    suspend fun countAll(): Int
}
