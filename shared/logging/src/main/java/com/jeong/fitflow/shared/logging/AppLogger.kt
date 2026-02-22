package com.jeong.fitflow.shared.logging

import javax.inject.Inject

interface AppLogger {
    fun debug(tag: String, message: String)
    fun debug(tag: String, message: () -> String)
    fun warning(tag: String, message: String, throwable: Throwable? = null)
}

interface NonFatalExceptionRecorder {
    fun record(throwable: Throwable)
}

class NoOpNonFatalExceptionRecorder @Inject constructor() : NonFatalExceptionRecorder {
    override fun record(throwable: Throwable) = Unit
}
