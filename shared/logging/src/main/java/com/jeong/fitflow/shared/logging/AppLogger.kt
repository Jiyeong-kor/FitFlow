package com.jeong.fitflow.shared.logging

interface AppLogger {
    fun debug(tag: String, message: String)
    fun warning(tag: String, message: String, throwable: Throwable? = null)
}
