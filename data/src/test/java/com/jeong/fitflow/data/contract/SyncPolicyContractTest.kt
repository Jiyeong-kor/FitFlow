package com.jeong.fitflow.data.contract

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncPolicyContractTest {

    @Test
    fun `sync registry defines required entities for restore`() {
        val entries = SyncPolicyRegistry.entries

        assertEquals(4, entries.size)
        entries.forEach { entry ->
            assertEquals(SyncPolicy.MUST_SYNC, entry.policy)
        }
    }
}
