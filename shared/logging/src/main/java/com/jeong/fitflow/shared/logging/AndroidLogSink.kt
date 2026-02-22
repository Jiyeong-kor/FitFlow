package com.jeong.fitflow.shared.logging

interface AndroidLogSink {
    fun debug(tag: String, message: String)

    fun warning(tag: String, message: String, throwable: Throwable?)
}
