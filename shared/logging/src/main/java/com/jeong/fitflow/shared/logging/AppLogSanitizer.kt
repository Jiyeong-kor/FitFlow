package com.jeong.fitflow.shared.logging

import javax.inject.Inject

class AppLogSanitizer @Inject constructor() {
    fun sanitizeTag(tag: String): String =
        tag.take(TAG_MAX_LENGTH).ifBlank { DEFAULT_TAG }

    fun sanitizeMessage(message: String): String =
        if (message.isBlank()) EMPTY_MESSAGE
        else SENSITIVE_PATTERNS.fold(message) { current, pattern ->
            pattern.replace(current, REDACTED_VALUE)
        }

    private companion object {
        const val DEFAULT_TAG = "App"
        const val EMPTY_MESSAGE = "(empty message)"
        const val REDACTED_VALUE = "[REDACTED]"
        const val TAG_MAX_LENGTH = 23

        val SENSITIVE_PATTERNS = listOf(
            Regex("(?i)(?<![\\w-])(?:api[_-]?key|token|secret|client[_-]?secret)\\b\\s*[:=]\\s*[^,\\s]+"),
            Regex("(?i)(?<![\\w-])authorization\\b(?!\\s*[:=]\\s*bearer\\b)\\s*[:=]\\s*[^,\\s]+"),
            Regex("(?i)bearer\\s*[:=]?\\s*[a-z0-9._~+\\-/]+=*"),
            Regex("(?i)(access|refresh)[_-]?token\\s*[:=]\\s*[^,\\s]+"),
            Regex("(?i)(?<![\\w-])(?:uid|user[_-]?id|email)\\s*[:=]\\s*[^,\\s]+"),
            Regex("(?i)(?<![\\w.-])/?(?:users|usernames|records|goals|sessions|running_reminders)/[a-z0-9._-]+(?:/[a-z0-9._-]+)*(?![\\w/.-])"),
            Regex("(?i)(?<![\\w.%+-])[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}(?![\\w-])")
        )
    }
}
