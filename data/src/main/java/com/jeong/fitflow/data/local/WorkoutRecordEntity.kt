package com.jeong.fitflow.data.local

import androidx.room.Entity
import com.jeong.fitflow.data.contract.RunningDatabaseContract

@Entity(
    tableName = RunningDatabaseContract.TABLE_WORKOUT_RECORD,
    primaryKeys = ["date", "exerciseType"]
)
data class WorkoutRecordEntity(
    val date: Long,
    val exerciseType: String,
    val repCount: Int
)
