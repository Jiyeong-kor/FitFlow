package com.jeong.runninggoaltracker.feature.home.presentation

import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.PeriodState
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.model.RunningSummary
import com.jeong.runninggoaltracker.domain.model.WorkoutRecord
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
        workoutRecords: List<WorkoutRecord>,
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
        val filteredWorkoutRecords = workoutRecords.filter { workoutRecord ->
            periodDateCalculator.isDateInPeriod(
                dateMillis = workoutRecord.date,
                period = period,
                selectedDateMillis = selectedDateState.dateMillis
            )
        }
        val activityDayStarts = (records.map { record ->
            periodDateCalculator.startOfDayMillis(record.date)
        } + workoutRecords.map { workoutRecord ->
            periodDateCalculator.startOfDayMillis(workoutRecord.date)
        }).toSet()
        val activityLogs = (
            filteredRecords.map { record ->
                HomeWorkoutLogUiModel(
                    id = record.id,
                    timestamp = record.date,
                    distanceKm = record.distanceKm,
                    repCount = 0,
                    durationMinutes = record.durationMinutes,
                    type = HomeWorkoutType.RUNNING
                )
            } + filteredWorkoutRecords.mapNotNull { workoutRecord ->
                val type = when (workoutRecord.exerciseType) {
                    ExerciseType.SQUAT -> HomeWorkoutType.SQUAT
                    ExerciseType.LUNGE -> HomeWorkoutType.LUNGE
                    else -> null
                } ?: return@mapNotNull null
                HomeWorkoutLogUiModel(
                    id = workoutRecord.date + workoutRecord.exerciseType.ordinal,
                    timestamp = workoutRecord.date,
                    distanceKm = 0.0,
                    repCount = workoutRecord.repCount,
                    durationMinutes = 0,
                    type = type
                )
            }
        ).sortedByDescending { it.timestamp }
        val summaryUiState = periodSummary.toUiState().copy(
            totalSquatCount = filteredWorkoutRecords
                .filter { it.exerciseType == ExerciseType.SQUAT }
                .sumOf { it.repCount },
            totalLungeCount = filteredWorkoutRecords
                .filter { it.exerciseType == ExerciseType.LUNGE }
                .sumOf { it.repCount }
        )
        val weeklyRange = dateRangeCalculator.weekRange(selectedDateState.dateMillis)
        return HomeUiState(
            periodState = period,
            selectedDateState = selectedDateState,
            isCalendarVisible = isCalendarVisible,
            calendarMonthState = calendarMonthState,
            calendarDays = calendarCalculator.buildCalendarDays(
                state = calendarMonthState,
                activityDayStarts = activityDayStarts
            ),
            weeklyRange = HomeWeeklyRange(
                startMillis = weeklyRange.first,
                endMillis = weeklyRange.second
            ),
            summary = summaryUiState,
            activityLogs = activityLogs,
            weeklyGoalKm = summary.weeklyGoalKm
        )
    }
}
