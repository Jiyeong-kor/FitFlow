package com.jeong.fitflow.feature.home.presentation

import com.jeong.fitflow.domain.model.PeriodState
import com.jeong.fitflow.domain.model.RunningRecord
import com.jeong.fitflow.domain.model.RunningSummary
import com.jeong.fitflow.domain.model.WorkoutRecord
import com.jeong.fitflow.domain.usecase.GetRunningRecordsUseCase
import com.jeong.fitflow.domain.usecase.GetRunningSummaryUseCase
import com.jeong.fitflow.domain.usecase.GetWorkoutRecordsUseCase
import com.jeong.fitflow.feature.home.domain.CalendarMonthState
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.catch

private data class HomeSummaryRecords(
    val summary: RunningSummary,
    val records: List<RunningRecord>,
    val workoutRecords: List<WorkoutRecord>
)

class HomeStateHolder @Inject constructor(
    private val getRunningSummaryUseCase: GetRunningSummaryUseCase,
    private val getRunningRecordsUseCase: GetRunningRecordsUseCase,
    private val getWorkoutRecordsUseCase: GetWorkoutRecordsUseCase,
    private val uiStateMapper: HomeUiStateMapper
) {
    fun initialState(
        period: PeriodState,
        selectedDateState: SelectedDateState,
        isCalendarVisible: Boolean,
        calendarMonthState: CalendarMonthState
    ): HomeUiState =
        uiStateMapper.map(
            summary = RunningSummary(),
            records = emptyList(),
            workoutRecords = emptyList(),
            period = period,
            selectedDateState = selectedDateState,
            isCalendarVisible = isCalendarVisible,
            calendarMonthState = calendarMonthState
        )

    fun uiState(
        periodState: StateFlow<PeriodState>,
        selectedDateState: StateFlow<SelectedDateState>,
        calendarVisibility: StateFlow<Boolean>,
        calendarMonthState: StateFlow<CalendarMonthState>,
        initialState: HomeUiState,
        scope: CoroutineScope
    ): StateFlow<HomeUiState> =
        combine(
            combine(
                getRunningSummaryUseCase(),
                getRunningRecordsUseCase(),
                getWorkoutRecordsUseCase()
            ) { summary, records, workoutRecords ->
                HomeSummaryRecords(
                    summary = summary,
                    records = records,
                    workoutRecords = workoutRecords
                )
            },
            periodState,
            selectedDateState,
            calendarVisibility,
            calendarMonthState
        ) { summaryRecords, period, selectedDate, isCalendarVisible, calendarMonth ->
            uiStateMapper.map(
                summary = summaryRecords.summary,
                records = summaryRecords.records,
                workoutRecords = summaryRecords.workoutRecords,
                period = period,
                selectedDateState = selectedDate,
                isCalendarVisible = isCalendarVisible,
                calendarMonthState = calendarMonth
            )
        }.catch {
            emit(initialState)
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = initialState
        )
}
