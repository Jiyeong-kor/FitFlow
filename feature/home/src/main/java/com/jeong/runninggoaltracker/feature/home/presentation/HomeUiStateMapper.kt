package com.jeong.runninggoaltracker.feature.home.presentation

import com.jeong.runninggoaltracker.domain.model.PeriodState
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.model.RunningSummary
import com.jeong.runninggoaltracker.domain.util.RunningPeriodDateCalculator
import com.jeong.runninggoaltracker.domain.util.RunningPeriodSummaryCalculator
import com.jeong.runninggoaltracker.feature.home.domain.CalendarMonthState
import com.jeong.runninggoaltracker.feature.home.domain.HomeCalendarCalculator
import com.jeong.runninggoaltracker.feature.home.domain.HomeDateRangeCalculator
import javax.inject.Inject

class HomeUiStateMapper @Inject constructor(
    private val periodDateCalculator: RunningPeriodDateCalculator,
    private val periodSummaryCalculator: RunningPeriodSummaryCalculator,
    private val calendarCalculator: HomeCalendarCalculator,
    private val dateRangeCalculator: HomeDateRangeCalculator
) {
    fun map(
        summary: RunningSummary,
        records: List<RunningRecord>,
        period: PeriodState,
        selectedDateState: SelectedDateState,
        isCalendarVisible: Boolean,
        calendarMonthState: CalendarMonthState
    ): HomeUiState {
        val filteredRecords = periodDateCalculator.filterByPeriod(
            records = records,
            period = period,
            selectedDateMillis = selectedDateState.dateMillis
        )
        val periodSummary = periodSummaryCalculator.calculate(filteredRecords)
        val weeklyRange = dateRangeCalculator.weekRange(selectedDateState.dateMillis)
        return HomeUiState(
            periodState = period,
            selectedDateState = selectedDateState,
            isCalendarVisible = isCalendarVisible,
            calendarMonthState = calendarMonthState,
            calendarDays = calendarCalculator.buildCalendarDays(calendarMonthState),
            weeklyRange = HomeWeeklyRange(
                startMillis = weeklyRange.first,
                endMillis = weeklyRange.second
            ),
            summary = periodSummary.toUiState(),
            activityLogs = filteredRecords.map { record ->
                HomeWorkoutLogUiModel(
                    id = record.id,
                    timestamp = record.date,
                    distanceKm = record.distanceKm,
                    repCount = 0,
                    durationMinutes = record.durationMinutes,
                    type = HomeWorkoutType.RUNNING
                )
            },
            weeklyGoalKm = summary.weeklyGoalKm
        )
    }
}
