package com.jeong.fitflow.shared.logging

import android.util.Log
import javax.inject.Inject

class LogcatAppLogger @Inject constructor(
    private val appLogSanitizer: AppLogSanitizer
) : AppLogger {
    override fun debug(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(
                appLogSanitizer.sanitizeTag(tag),
                appLogSanitizer.sanitizeMessage(message)
            )
        }
    }

    override fun warning(tag: String, message: String, throwable: Throwable?) {
        Log.w(
            appLogSanitizer.sanitizeTag(tag),
            appLogSanitizer.sanitizeMessage(message),
            throwable
        )
    }
}
