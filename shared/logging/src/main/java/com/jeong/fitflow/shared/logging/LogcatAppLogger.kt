package com.jeong.fitflow.shared.logging

import javax.inject.Inject

class LogcatAppLogger @Inject constructor(
    private val appLogSanitizer: AppLogSanitizer,
    private val appBuildInfo: AppBuildInfo,
    private val androidLogSink: AndroidLogSink,
    private val nonFatalExceptionRecorder: NonFatalExceptionRecorder
) : AppLogger {
    override fun debug(tag: String, message: String) {
        if (appBuildInfo.isDebugBuild()) {
            androidLogSink.debug(
                appLogSanitizer.sanitizeTag(tag),
                appLogSanitizer.sanitizeMessage(message)
            )
        }
    }

    override fun debug(tag: String, message: () -> String) {
        if (appBuildInfo.isDebugBuild()) {
            androidLogSink.debug(
                appLogSanitizer.sanitizeTag(tag),
                appLogSanitizer.sanitizeMessage(message())
            )
        }
    }

    override fun warning(tag: String, message: String, throwable: Throwable?) {
        val sanitizedTag = appLogSanitizer.sanitizeTag(tag)
        val sanitizedMessage = appLogSanitizer.sanitizeMessage(message)
        if (appBuildInfo.isDebugBuild()) {
            androidLogSink.warning(sanitizedTag, sanitizedMessage, throwable)
        } else {
            throwable?.let(nonFatalExceptionRecorder::record)
        }
    }
}
