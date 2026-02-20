package com.jeong.fitflow.shared.logging

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLogSanitizerTest {
    private val sanitizer = AppLogSanitizer()

    @Test
    fun sanitizeMessage_redactsSensitiveTokens() {
        val source = "authorization=abc token=xyz bearer qwerty users/test-user"

        val sanitized = sanitizer.sanitizeMessage(source)

        assertEquals("[REDACTED] [REDACTED] [REDACTED] [REDACTED]", sanitized)
    }

    @Test
    fun sanitizeMessage_returnsPlaceholderWhenBlank() {
        val sanitized = sanitizer.sanitizeMessage("  ")

        assertEquals("(empty message)", sanitized)
    }

    @Test
    fun sanitizeTag_appliesAndroidTagLimit() {
        val sanitized = sanitizer.sanitizeTag("ThisTagNameIsWayTooLongForAndroidLog")

        assertEquals("ThisTagNameIsWayTooLong", sanitized)
    }
}
