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
    fun sanitizeMessage_redactsUserIdentifiersAndFirestorePaths() {
        val source = "uid=test-uid userId=user-123 records/test-uid/goals/goal-1"

        val sanitized = sanitizer.sanitizeMessage(source)

        assertEquals("[REDACTED] [REDACTED] [REDACTED]", sanitized)
    }

    @Test
    fun sanitizeMessage_redactsBearerTokenWithUrlSafeAndPaddingCharacters() {
        val source = "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.a-b_c~d+e/f=="

        val sanitized = sanitizer.sanitizeMessage(source)

        assertEquals("Authorization: [REDACTED]", sanitized)
    }

    @Test
    fun sanitizeMessage_redactsClientSecretAccessTokenAndRefreshToken() {
        val source = "clientSecret=my-secret accessToken=my-access-token refreshToken=my-refresh-token"

        val sanitized = sanitizer.sanitizeMessage(source)

        assertEquals("[REDACTED] [REDACTED] [REDACTED]", sanitized)
    }

    @Test
    fun sanitizeMessage_redactsEmailAndReminderPath() {
        val source = "email=test@example.com running_reminders/reminder-1"

        val sanitized = sanitizer.sanitizeMessage(source)

        assertEquals("[REDACTED] [REDACTED]", sanitized)
    }

    @Test
    fun sanitizeMessage_redactsPathWithLeadingSlash() {
        val source = "Synced path: /users/test-user/running_reminders/r-1"

        val sanitized = sanitizer.sanitizeMessage(source)

        assertEquals("Synced path: [REDACTED]", sanitized)
    }

    @Test
    fun sanitizeMessage_redactsPathAndEmailWhenWrappedWithPunctuation() {
        val source = "(Users/User.1/running_reminders/r-1), contact: test.user+alert@example.com."

        val sanitized = sanitizer.sanitizeMessage(source)

        assertEquals("([REDACTED]), contact: [REDACTED].", sanitized)
    }

    @Test
    fun sanitizeMessage_redactsFirestoreDocumentsSubPath() {
        val source = "Firestore URL path: projects/p/databases/(default)/documents/users/test-user/running_reminders/r-1"

        val sanitized = sanitizer.sanitizeMessage(source)

        assertEquals("Firestore URL path: projects/p/databases/(default)/documents/[REDACTED]", sanitized)
    }

    @Test
    fun sanitizeMessage_keepsNonSensitiveEmailLikeText() {
        val source = "not-an-email: local@dev"

        val sanitized = sanitizer.sanitizeMessage(source)

        assertEquals("not-an-email: local@dev", sanitized)
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
