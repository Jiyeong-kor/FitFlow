package com.jeong.runninggoaltracker.feature.home.presentation

import com.jeong.runninggoaltracker.domain.model.PeriodState
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.model.RunningSummary
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRecordsUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningSummaryUseCase
import com.jeong.runninggoaltracker.feature.home.domain.CalendarMonthState
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private data class HomeSummaryRecords(
    val summary: RunningSummary,
    val records: List<RunningRecord>
)

class HomeStateHolder @Inject constructor(
    private val getRunningSummaryUseCase: GetRunningSummaryUseCase,
    private val getRunningRecordsUseCase: GetRunningRecordsUseCase,
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
                getRunningRecordsUseCase()
            ) { summary, records ->
                HomeSummaryRecords(summary = summary, records = records)
            },
            periodState,
            selectedDateState,
            calendarVisibility,
            calendarMonthState
        ) { summaryRecords, period, selectedDate, isCalendarVisible, calendarMonth ->
            uiStateMapper.map(
                summary = summaryRecords.summary,
                records = summaryRecords.records,
                period = period,
                selectedDateState = selectedDate,
                isCalendarVisible = isCalendarVisible,
                calendarMonthState = calendarMonth
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = initialState
        )
}
