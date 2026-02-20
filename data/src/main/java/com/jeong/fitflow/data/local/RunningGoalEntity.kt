package com.jeong.fitflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jeong.fitflow.data.contract.RunningDatabaseContract

@Entity(tableName = RunningDatabaseContract.TABLE_RUNNING_GOAL)
data class RunningGoalEntity(
    @PrimaryKey val id: Int = 0,
    val weeklyGoalKm: Double
)
