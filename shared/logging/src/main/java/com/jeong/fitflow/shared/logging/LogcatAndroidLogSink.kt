package com.jeong.fitflow.shared.logging

import android.util.Log
import javax.inject.Inject

class LogcatAndroidLogSink @Inject constructor() : AndroidLogSink {
    override fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun warning(tag: String, message: String, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }
}
