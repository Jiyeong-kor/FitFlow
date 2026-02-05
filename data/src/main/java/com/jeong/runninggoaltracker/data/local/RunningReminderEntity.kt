package com.jeong.runninggoaltracker.data.local

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.jeong.runninggoaltracker.data.contract.RunningDatabaseContract

@Entity(tableName = RunningDatabaseContract.TABLE_RUNNING_REMINDER)
data class RunningReminderEntity(
    @PrimaryKey val id: Int? = null,
    val hour: Int,
    val minute: Int,
    @ColumnInfo(name = "enabled")
    val isEnabled: Boolean,
    val days: Set<Int>
)
