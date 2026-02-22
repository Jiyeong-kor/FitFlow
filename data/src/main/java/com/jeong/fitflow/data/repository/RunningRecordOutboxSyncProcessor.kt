package com.jeong.fitflow.data.repository

import com.jeong.fitflow.data.local.SyncOutboxEntity
import com.jeong.fitflow.data.local.SyncOutboxType
import com.jeong.fitflow.shared.logging.AppLogger
import com.jeong.fitflow.domain.util.DateProvider

internal class RunningRecordOutboxSyncProcessor(
    private val appLogger: AppLogger,
    private val dateProvider: DateProvider
) {
    suspend fun flush(
        pending: List<SyncOutboxEntity>,
        upload: suspend (SyncOutboxEntity) -> Unit,
        onDelete: suspend (SyncOutboxEntity) -> Unit,
        onUpdateRetry: suspend (SyncOutboxEntity, Int, Long) -> Unit
    ) {
        val nowMs = dateProvider.getToday()
        for (entry in pending) {
            if (entry.syncType != SyncOutboxType.RUNNING_RECORD) continue
            if (entry.retryCount >= SyncRetryPolicy.MAX_RETRY_LIMIT) {
                appLogger.warning(LOG_TAG, RETRY_LIMIT_REACHED_MESSAGE, null)
                continue
            }
            if (!SyncRetryPolicy.isRetryAllowed(entry.retryCount, nowMs, entry.nextRetryAt)) continue
            try {
                upload(entry)
                onDelete(entry)
            } catch (exception: Exception) {
                val nextRetryCount = entry.retryCount + 1
                val nextRetryAt = SyncRetryPolicy.computeNextRetryAt(nowMs, nextRetryCount)
                onUpdateRetry(entry, nextRetryCount, nextRetryAt)
                if (nextRetryCount >= SyncRetryPolicy.MAX_RETRY_LIMIT) {
                    appLogger.warning(LOG_TAG, RETRY_LIMIT_REACHED_MESSAGE, exception)
                }
            }
        }
    }

    private companion object {
        const val LOG_TAG = "RunningRecordRepo"
        const val RETRY_LIMIT_REACHED_MESSAGE = "Outbox retry limit reached; leaving entry pending"
    }
}
