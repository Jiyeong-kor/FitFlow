package com.jeong.fitflow.domain.model

data class RunningElapsedTime(
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val shouldShowHours: Boolean
)
