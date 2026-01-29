package com.jeong.runninggoaltracker.feature.record.presentation

import com.jeong.runninggoaltracker.domain.model.RunningElapsedTime
import com.jeong.runninggoaltracker.domain.model.RunningPace

fun RunningElapsedTime.toUiState(): RecordElapsedTimeUiState = RecordElapsedTimeUiState(
    hours = hours,
    minutes = minutes,
    seconds = seconds,
    showHours = showHours
)

fun RunningPace.toUiState(): RecordPaceUiState = RecordPaceUiState(
    minutes = minutes,
    seconds = seconds,
    isAvailable = isAvailable
)
