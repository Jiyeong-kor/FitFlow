package com.jeong.runninggoaltracker.domain.model

data class RunningElapsedTime(
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val showHours: Boolean
)
