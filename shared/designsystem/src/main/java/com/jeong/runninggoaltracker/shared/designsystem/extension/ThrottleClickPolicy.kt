package com.jeong.runninggoaltracker.shared.designsystem.extension

object ThrottleClickPolicy {
    fun isClickAllowed(
        currentTimeMillis: Long,
        lastExecutionTimeMillis: Long,
        intervalMillis: Long
    ): Boolean = currentTimeMillis - lastExecutionTimeMillis > intervalMillis
}
