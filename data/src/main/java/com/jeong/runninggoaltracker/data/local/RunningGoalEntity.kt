package com.jeong.runninggoaltracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jeong.runninggoaltracker.data.contract.RunningDatabaseContract

@Entity(tableName = RunningDatabaseContract.TABLE_RUNNING_GOAL)
data class RunningGoalEntity(
    @PrimaryKey val id: Int = 0,
    val weeklyGoalKm: Double
)
