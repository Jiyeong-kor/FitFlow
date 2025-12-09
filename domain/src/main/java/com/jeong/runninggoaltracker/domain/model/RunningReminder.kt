package com.jeong.runninggoaltracker.domain.model

data class RunningReminder(
    val id: Int? = null,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean,
    val days: Set<Int> = emptySet()
)
