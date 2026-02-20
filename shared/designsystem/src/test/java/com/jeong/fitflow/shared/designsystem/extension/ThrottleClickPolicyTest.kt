package com.jeong.fitflow.shared.designsystem.extension

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThrottleClickPolicyTest {

    @Test
    fun `interval not elapsed returns false`() {
        val result = ThrottleClickPolicy.isClickAllowed(
            currentTimeMillis = 1_000L,
            lastExecutionTimeMillis = 500L,
            intervalMillis = 700L
        )

        assertFalse(result)
    }

    @Test
    fun `interval elapsed returns true`() {
        val result = ThrottleClickPolicy.isClickAllowed(
            currentTimeMillis = 2_000L,
            lastExecutionTimeMillis = 500L,
            intervalMillis = 700L
        )

        assertTrue(result)
    }
}
