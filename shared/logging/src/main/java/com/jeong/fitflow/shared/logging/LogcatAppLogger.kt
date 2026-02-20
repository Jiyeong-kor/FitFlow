package com.jeong.fitflow.shared.logging

import android.util.Log
import javax.inject.Inject

class LogcatAppLogger @Inject constructor() : AppLogger {
    override fun debug(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    override fun warning(tag: String, message: String, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }
}
