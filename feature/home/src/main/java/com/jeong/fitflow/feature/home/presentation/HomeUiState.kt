package com.jeong.fitflow.feature.home.presentation

import com.jeong.fitflow.domain.model.PeriodState
import com.jeong.fitflow.feature.home.domain.CalendarDay
import com.jeong.fitflow.feature.home.domain.CalendarMonthState


data class HomeUiState(
    val periodState: PeriodState = PeriodState.DAILY,
    val selectedDateState: SelectedDateState,
    val isCalendarVisible: Boolean = false,
    val calendarMonthState: CalendarMonthState,
    val calendarDays: List<CalendarDay?> = emptyList(),
    val weeklyRange: HomeWeeklyRange,
    val summary: HomeSummaryUiState = HomeSummaryUiState(),
    val activityLogs: List<HomeWorkoutLogUiModel> = emptyList(),
    val weeklyGoalKm: Double? = null
)

data class HomeWeeklyRange(
    val startMillis: Long,
    val endMillis: Long
)
