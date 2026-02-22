package com.jeong.fitflow.data.repository

object SyncRetryPolicy {
    const val MAX_RETRY_LIMIT = 5
    const val BASE_BACKOFF_MS = 1_000L
    const val MAX_BACKOFF_MS = 60_000L

    fun isRetryAllowed(retryCount: Int, nowMs: Long, nextRetryAt: Long): Boolean =
        retryCount < MAX_RETRY_LIMIT && nowMs >= nextRetryAt

    fun computeBackoffDelayMs(nextRetryCount: Int): Long {
        val exponent = nextRetryCount.coerceAtMost(30)
        val rawDelay = BASE_BACKOFF_MS * (1L shl exponent)
        return rawDelay.coerceAtMost(MAX_BACKOFF_MS)
    }

    fun computeNextRetryAt(nowMs: Long, nextRetryCount: Int): Long =
        nowMs + computeBackoffDelayMs(nextRetryCount)
}
