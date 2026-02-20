package com.jeong.fitflow.feature.home.presentation

import com.jeong.fitflow.domain.model.ExerciseType
import com.jeong.fitflow.domain.model.PeriodState
import com.jeong.fitflow.domain.model.RunningRecord
import com.jeong.fitflow.domain.model.RunningSummary
import com.jeong.fitflow.domain.model.WorkoutRecord
import com.jeong.fitflow.domain.util.RunningPeriodDateCalculator
import com.jeong.fitflow.domain.util.RunningMetricsCalculator
import com.jeong.fitflow.domain.util.RunningPeriodSummaryCalculator
import com.jeong.fitflow.feature.home.domain.HomeCalendarCalculator
import com.jeong.fitflow.feature.home.domain.HomeDateRangeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeUiStateMapperTest {

    private val mapper = HomeUiStateMapper(
        periodDateCalculator = RunningPeriodDateCalculator(),
        periodSummaryCalculator = RunningPeriodSummaryCalculator(
            metricsCalculator = RunningMetricsCalculator()
        ),
        calendarCalculator = HomeCalendarCalculator(),
        dateRangeCalculator = HomeDateRangeCalculator()
    )

    @Test
    fun map_includes_mixed_workout_logs_and_summary_counts() {
        val selectedDate = 1_700_000_000_000L
        val runningRecords = listOf(
            RunningRecord(id = 1L, date = selectedDate, distanceKm = 3.0, durationMinutes = 20)
        )
        val workoutRecords = listOf(
            WorkoutRecord(date = selectedDate + 1_000L, exerciseType = ExerciseType.LUNGE, repCount = 12),
            WorkoutRecord(date = selectedDate + 2_000L, exerciseType = ExerciseType.SQUAT, repCount = 15)
        )

        val uiState = mapper.map(
            summary = RunningSummary(),
            records = runningRecords,
            workoutRecords = workoutRecords,
            period = PeriodState.DAILY,
            selectedDateState = SelectedDateState(selectedDate),
            isCalendarVisible = false,
            calendarMonthState = HomeCalendarCalculator().monthStateFromMillis(selectedDate)
        )

        assertEquals(15, uiState.summary.totalSquatCount)
        assertEquals(12, uiState.summary.totalLungeCount)
        assertTrue(uiState.activityLogs.any { it.type == HomeWorkoutType.RUNNING })
        assertTrue(uiState.activityLogs.any { it.type == HomeWorkoutType.SQUAT })
        assertTrue(uiState.activityLogs.any { it.type == HomeWorkoutType.LUNGE })
    }
}
