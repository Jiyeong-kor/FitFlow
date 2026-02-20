package com.jeong.fitflow.feature.record.presentation

import com.jeong.fitflow.domain.model.RunningElapsedTime
import com.jeong.fitflow.domain.model.RunningPace

fun RunningElapsedTime.toUiState(): RecordElapsedTimeUiState = RecordElapsedTimeUiState(
    hours = hours,
    minutes = minutes,
    seconds = seconds,
    shouldShowHours = shouldShowHours
)

fun RunningPace.toUiState(): RecordPaceUiState = RecordPaceUiState(
    minutes = minutes,
    seconds = seconds,
    isAvailable = isAvailable
)
