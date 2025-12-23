package com.jeong.runninggoaltracker.domain.model

import com.jeong.runninggoaltracker.domain.model.time.AppDate

data class RunningRecord(
    val id: Long = 0L,
    val date: AppDate,
    val distanceKm: Double,
    val durationMinutes: Int
) {
    init {
        require(distanceKm > 0.0)
        require(durationMinutes > 0)
    }
}
