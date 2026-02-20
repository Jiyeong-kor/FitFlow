package com.jeong.fitflow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jeong.fitflow.data.contract.RunningDatabaseContract

@Dao
interface SyncOutboxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SyncOutboxEntity)

    @Query(
        "SELECT * FROM ${RunningDatabaseContract.TABLE_SYNC_OUTBOX} " +
            "ORDER BY createdAt ASC LIMIT :limit"
    )
    suspend fun getPending(limit: Int): List<SyncOutboxEntity>

    @Query(
        "DELETE FROM ${RunningDatabaseContract.TABLE_SYNC_OUTBOX} " +
            "WHERE syncType = :syncType AND docId = :docId"
    )
    suspend fun delete(syncType: String, docId: String)

    @Query(
        "UPDATE ${RunningDatabaseContract.TABLE_SYNC_OUTBOX} " +
            "SET retryCount = retryCount + 1 WHERE syncType = :syncType AND docId = :docId"
    )
    suspend fun incrementRetry(syncType: String, docId: String)
}
