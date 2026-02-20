package com.jeong.fitflow.domain.model

data class RunningSummary(
    val weeklyGoalKm: Double? = null,
    val totalThisWeekKm: Double = 0.0,
    val recordCountThisWeek: Int = 0,
    val progress: Float = 0f
)
