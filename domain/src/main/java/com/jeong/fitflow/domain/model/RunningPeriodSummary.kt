package com.jeong.fitflow.domain.model

data class RunningPeriodSummary(
    val totalDistanceKm: Double,
    val totalCalories: Int,
    val totalDurationMinutes: Int,
    val averagePace: RunningPace
)
