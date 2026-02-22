package com.jeong.fitflow.shared.logging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LogcatAppLoggerTest {
    @Test
    fun `debug logs when debug build is enabled`() {
        val androidLogSink = FakeAndroidLogSink()
        val recorder = FakeNonFatalExceptionRecorder()
        val logger = LogcatAppLogger(
            appLogSanitizer = AppLogSanitizer(),
            appBuildInfo = FakeAppBuildInfo(isDebug = true),
            androidLogSink = androidLogSink,
            nonFatalExceptionRecorder = recorder
        )

        logger.debug(tag = "FeatureAuth", message = "token: secret")

        assertEquals(1, androidLogSink.debugLogs.size)
        assertTrue(androidLogSink.debugLogs.first().second.contains("[REDACTED]"))
        assertNull(recorder.recordedThrowable)
    }

    @Test
    fun `warning records non-fatal exception in release build and skips logcat warning`() {
        val androidLogSink = FakeAndroidLogSink()
        val recorder = FakeNonFatalExceptionRecorder()
        val logger = LogcatAppLogger(
            appLogSanitizer = AppLogSanitizer(),
            appBuildInfo = FakeAppBuildInfo(isDebug = false),
            androidLogSink = androidLogSink,
            nonFatalExceptionRecorder = recorder
        )
        val throwable = IllegalStateException("sync failed")

        logger.warning(tag = "Firestore", message = "uid=user123", throwable = throwable)

        assertEquals(0, androidLogSink.warningLogs.size)
        assertEquals(throwable, recorder.recordedThrowable)
    }
}

private class FakeAppBuildInfo(private val isDebug: Boolean) : AppBuildInfo {
    override fun isDebugBuild(): Boolean = isDebug
}

private class FakeAndroidLogSink : AndroidLogSink {
    val debugLogs = mutableListOf<Pair<String, String>>()
    val warningLogs = mutableListOf<Triple<String, String, Throwable?>>()

    override fun debug(tag: String, message: String) {
        debugLogs += tag to message
    }

    override fun warning(tag: String, message: String, throwable: Throwable?) {
        warningLogs += Triple(tag, message, throwable)
    }
}

private class FakeNonFatalExceptionRecorder : NonFatalExceptionRecorder {
    var recordedThrowable: Throwable? = null

    override fun record(throwable: Throwable) {
        recordedThrowable = throwable
    }
}
