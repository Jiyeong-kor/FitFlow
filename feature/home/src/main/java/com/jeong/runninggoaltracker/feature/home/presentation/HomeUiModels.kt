package com.jeong.runninggoaltracker.feature.home.presentation

import androidx.annotation.StringRes

data class ActivityRecognitionUiState(
    @field:StringRes val labelResId: Int? = null
)

data class ActivityLogUiModel(
    val time: Long,
    @field:StringRes val labelResId: Int
)

data class HomeRecentActivityUiModel(
    val timestamp: Long,
    val month: Int,
    val day: Int,
    val dayOfWeek: Int,
    @field:StringRes val typeResId: Int
)
