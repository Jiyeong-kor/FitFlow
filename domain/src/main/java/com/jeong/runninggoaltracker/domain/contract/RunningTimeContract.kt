package com.jeong.runninggoaltracker.domain.contract

object RunningTimeContract {
    const val MILLIS_PER_SECOND = 1000L
    const val SECONDS_PER_MINUTE = 60L
    const val MINUTES_PER_HOUR = 60L
    const val SECONDS_PER_HOUR = 3600L
    const val MILLIS_PER_MINUTE = MILLIS_PER_SECOND * SECONDS_PER_MINUTE
    const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    const val ZERO_LONG = 0L
    const val ZERO_DOUBLE = 0.0
}
