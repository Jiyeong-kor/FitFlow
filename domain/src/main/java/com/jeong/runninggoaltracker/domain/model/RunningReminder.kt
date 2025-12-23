package com.jeong.runninggoaltracker.domain.model

import com.jeong.runninggoaltracker.domain.model.time.AppDayOfWeek


data class RunningReminder(
    val id: Int? = null,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean,
    val days: Set<AppDayOfWeek> = emptySet()
) {
    init {
        require(hour in 0..23)
        require(minute in 0..59)
    }
}
