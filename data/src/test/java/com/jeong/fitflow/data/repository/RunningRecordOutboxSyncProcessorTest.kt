package com.jeong.fitflow.data.repository

import com.jeong.fitflow.data.local.SyncOutboxEntity
import com.jeong.fitflow.data.local.SyncOutboxType
import com.jeong.fitflow.shared.logging.AppLogger
import com.jeong.fitflow.domain.util.DateProvider
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RunningRecordOutboxSyncProcessorTest {

    private val logger = mockk<AppLogger>(relaxed = true)

    @Test
    fun `retryCount lower than limit is allowed`() {
        assertTrue(SyncRetryPolicy.isRetryAllowed(0, nowMs = 1_000L, nextRetryAt = 1_000L))
    }

    @Test
    fun `retryCount at limit is skipped`() {
        val allowed = SyncRetryPolicy.isRetryAllowed(
            SyncRetryPolicy.MAX_RETRY_LIMIT,
            nowMs = 1_000L,
            nextRetryAt = 0L
        )
        assertEquals(false, allowed)
    }

    @Test
    fun `backoff increases exponentially`() {
        val delay1 = SyncRetryPolicy.computeBackoffDelayMs(1)
        val delay2 = SyncRetryPolicy.computeBackoffDelayMs(2)
        assertEquals(delay1 * 2, delay2)
    }

    @Test
    fun `backoff is capped at max backoff`() {
        val delay = SyncRetryPolicy.computeBackoffDelayMs(20)
        assertEquals(SyncRetryPolicy.MAX_BACKOFF_MS, delay)
    }

    @Test
    fun `success deletes entry`() = runBlocking {
        val processor = RunningRecordOutboxSyncProcessor(logger, FakeDateProvider(1_000L))
        val entry = baseEntry(retryCount = 0)
        val deleted = mutableListOf<SyncOutboxEntity>()

        processor.flush(
            pending = listOf(entry),
            upload = {},
            onDelete = { deleted += it },
            onUpdateRetry = { _, _, _ -> throw AssertionError("retry should not update") }
        )

        assertEquals(listOf(entry), deleted)
    }

    @Test
    fun `failure updates retryCount and nextRetryAt`() = runBlocking {
        val now = 1_000L
        val processor = RunningRecordOutboxSyncProcessor(logger, FakeDateProvider(now))
        val entry = baseEntry(retryCount = 1)
        var updatedRetryCount = -1
        var updatedNextRetryAt = -1L

        processor.flush(
            pending = listOf(entry),
            upload = { throw IllegalStateException("offline") },
            onDelete = { throw AssertionError("delete should not be called") },
            onUpdateRetry = { _, retryCount, nextRetryAt ->
                updatedRetryCount = retryCount
                updatedNextRetryAt = nextRetryAt
            }
        )

        assertEquals(2, updatedRetryCount)
        assertEquals(now + SyncRetryPolicy.computeBackoffDelayMs(2), updatedNextRetryAt)
    }

    private fun baseEntry(retryCount: Int): SyncOutboxEntity = SyncOutboxEntity(
        syncType = SyncOutboxType.RUNNING_RECORD,
        docId = "doc",
        date = 100L,
        distanceKm = 1.0,
        durationMinutes = 10,
        retryCount = retryCount,
        nextRetryAt = 0L,
        createdAt = 0L
    )

    private class FakeDateProvider(private val nowMs: Long) : DateProvider {
        override fun getToday(): Long = nowMs
        override fun getTodayFlow() = throw UnsupportedOperationException()
        override fun getStartOfWeek(timestamp: Long) = throw UnsupportedOperationException()
    }
}
