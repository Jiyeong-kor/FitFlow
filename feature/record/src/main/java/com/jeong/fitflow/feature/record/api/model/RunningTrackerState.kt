package com.jeong.fitflow.feature.record.api.model

data class RunningTrackerState(
    val isTracking: Boolean = false,
    val distanceKm: Double = 0.0,
    val elapsedMillis: Long = 0L,
    val startedAtEpochMillis: Long = 0L,
    val updatedAtEpochMillis: Long = 0L,
    val isPermissionRequired: Boolean = false
)
