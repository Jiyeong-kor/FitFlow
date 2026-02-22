package com.jeong.fitflow.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jeong.fitflow.data.contract.RunningDatabaseContract

@Entity(
    tableName = RunningDatabaseContract.TABLE_SYNC_OUTBOX,
    indices = [Index(value = ["syncType", "docId"], unique = true)]
)
data class SyncOutboxEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val syncType: String,
    val docId: String,
    val date: Long,
    val distanceKm: Double? = null,
    val durationMinutes: Int? = null,
    val exerciseType: String? = null,
    val repCount: Int? = null,
    val retryCount: Int = 0,
    val nextRetryAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
)

object SyncOutboxType {
    const val RUNNING_RECORD = "RUNNING_RECORD"
    const val WORKOUT_RECORD = "WORKOUT_RECORD"
}
