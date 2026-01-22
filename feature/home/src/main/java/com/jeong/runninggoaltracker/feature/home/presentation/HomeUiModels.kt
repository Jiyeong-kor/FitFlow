package com.jeong.runninggoaltracker.feature.home.presentation

import androidx.annotation.StringRes

data class ActivityRecognitionUiState(
    @field:StringRes val labelResId: Int? = null
)

data class ActivityLogUiModel(
    val time: Long,
    @field:StringRes val labelResId: Int
)

enum class PeriodState {
    DAILY,
    WEEKLY,
    MONTHLY
}

data class SelectedDateState(
    val dateMillis: Long
)

enum class HomeWorkoutType {
    RUNNING,
    SQUAT
}

data class HomeWorkoutLogUiModel(
    val id: Long,
    val timestamp: Long,
    val distanceKm: Double,
    val durationMinutes: Int,
    val type: HomeWorkoutType,
    @field:StringRes val typeLabelResId: Int
)

data class HomeSummaryUiState(
    val totalDistanceKm: Double = 0.0,
    val totalCalories: Int = 0,
    val totalDurationMinutes: Int = 0,
    val averagePace: HomePaceUiState = HomePaceUiState()
)

data class HomePaceUiState(
    val minutes: Int = 0,
    val seconds: Int = 0,
    val isAvailable: Boolean = false
)
