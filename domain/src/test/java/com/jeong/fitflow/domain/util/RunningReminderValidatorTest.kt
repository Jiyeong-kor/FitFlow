package com.jeong.fitflow.domain.util

import com.jeong.fitflow.domain.model.RunningReminder
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RunningReminderValidatorTest {

    private val validator = RunningReminderValidator()

    @Test
    fun `normalizeEnabledDays disables reminder when enabled days empty`() {
        val reminder = RunningReminder(
            id = 1,
            hour = 8,
            minute = 30,
            isEnabled = true,
            days = emptySet()
        )

        val normalized = validator.normalizeEnabledDays(reminder)

        assertFalse(normalized.isEnabled)
    }

    @Test
    fun `normalizeEnabledDays keeps enabled when days exist`() {
        val reminder = RunningReminder(
            id = 1,
            hour = 8,
            minute = 30,
            isEnabled = true,
            days = setOf(1)
        )

        val normalized = validator.normalizeEnabledDays(reminder)

        assertTrue(normalized.isEnabled)
    }
}
