package com.jeong.runninggoaltracker.feature.record.presentation

import com.jeong.runninggoaltracker.domain.model.RunningRecord

data class RecordUiState(
    val records: List<RunningRecord> = emptyList(),
    val activityLabel: String = "UNKNOWN",
    val isTracking: Boolean = false,
    val distanceKm: Double = 0.0,
    val elapsedMillis: Long = 0L,
    val permissionRequired: Boolean = false
)
