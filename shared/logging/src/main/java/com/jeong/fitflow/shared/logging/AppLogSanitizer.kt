package com.jeong.fitflow.shared.logging

import javax.inject.Inject

class AppLogSanitizer @Inject constructor() {
    fun sanitizeTag(tag: String): String =
        tag.take(TAG_MAX_LENGTH).ifBlank { DEFAULT_TAG }

    fun sanitizeMessage(message: String): String {
        if (message.isBlank()) return EMPTY_MESSAGE
        return SENSITIVE_PATTERNS.fold(message) { current, pattern ->
            pattern.replace(current, REDACTED_VALUE)
        }
    }

    private companion object {
        const val DEFAULT_TAG = "App"
        const val EMPTY_MESSAGE = "(empty message)"
        const val REDACTED_VALUE = "[REDACTED]"
        const val TAG_MAX_LENGTH = 23

        val SENSITIVE_PATTERNS = listOf(
            Regex("(?i)(api[_-]?key|token|secret|authorization)\\s*[:=]\\s*[^,\\s]+"),
            Regex("(?i)bearer\\s+[a-z0-9._\\-]+"),
            Regex("(?i)(uid|user[_-]?id)\\s*[:=]\\s*[^,\\s]+"),
            Regex("(?i)(?<!\\S)(?:users|usernames|records|goals|sessions)/[a-z0-9_-]+(?:/[a-z0-9_-]+)*")
        )
    }
}
