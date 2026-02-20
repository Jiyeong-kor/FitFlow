package com.jeong.fitflow.feature.home.presentation

import com.jeong.fitflow.domain.model.RunningPace
import com.jeong.fitflow.domain.model.RunningPeriodSummary

fun RunningPeriodSummary.toUiState(): HomeSummaryUiState = HomeSummaryUiState(
    totalDistanceKm = totalDistanceKm,
    totalCalories = totalCalories,
    totalDurationMinutes = totalDurationMinutes,
    averagePace = averagePace.toUiState()
)

private fun RunningPace.toUiState(): HomePaceUiState = HomePaceUiState(
    minutes = minutes,
    seconds = seconds,
    isAvailable = isAvailable
)
