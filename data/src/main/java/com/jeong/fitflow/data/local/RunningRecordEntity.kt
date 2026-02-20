package com.jeong.fitflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jeong.fitflow.data.contract.RunningDatabaseContract

@Entity(tableName = RunningDatabaseContract.TABLE_RUNNING_RECORD)
data class RunningRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val date: Long,
    val distanceKm: Double,
    val durationMinutes: Int
)
