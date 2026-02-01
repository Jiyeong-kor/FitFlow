package com.jeong.runninggoaltracker.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.PeriodState
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.model.RunningSummary
import com.jeong.runninggoaltracker.domain.usecase.GetRunningSummaryUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRecordsUseCase
import com.jeong.runninggoaltracker.domain.util.DateProvider
import com.jeong.runninggoaltracker.domain.util.RunningPeriodDateCalculator
import com.jeong.runninggoaltracker.feature.home.domain.CalendarDay
import com.jeong.runninggoaltracker.feature.home.domain.CalendarMonthState
import com.jeong.runninggoaltracker.feature.home.domain.HomeCalendarCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val periodState: PeriodState = PeriodState.DAILY,
    val selectedDateState: SelectedDateState,
    val isCalendarVisible: Boolean = false,
    val calendarMonthState: CalendarMonthState,
    val calendarDays: List<CalendarDay?> = emptyList(),
    val summary: HomeSummaryUiState = HomeSummaryUiState(),
    val activityLogs: List<HomeWorkoutLogUiModel> = emptyList(),
    val weeklyGoalKm: Double? = null
)

sealed interface HomeUiEffect {
    data object NavigateToRecord : HomeUiEffect
    data object NavigateToGoal : HomeUiEffect
    data object NavigateToReminder : HomeUiEffect
}

private data class HomeSummaryRecords(
    val summary: RunningSummary,
    val records: List<RunningRecord>
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getRunningSummaryUseCase: GetRunningSummaryUseCase,
    getRunningRecordsUseCase: GetRunningRecordsUseCase,
    dateProvider: DateProvider,
    private val periodDateCalculator: RunningPeriodDateCalculator,
    private val uiStateMapper: HomeUiStateMapper,
    private val calendarCalculator: HomeCalendarCalculator
) : ViewModel() {

    private val periodState = MutableStateFlow(PeriodState.DAILY)
    private val selectedDateState = MutableStateFlow(
        SelectedDateState(dateMillis = periodDateCalculator.startOfDayMillis(dateProvider.getToday()))
    )
    private val calendarVisibility = MutableStateFlow(false)
    private val calendarMonthState = MutableStateFlow(
        calendarCalculator.monthStateFromMillis(dateProvider.getToday())
    )

    private val _effect = MutableSharedFlow<HomeUiEffect>()
    val effect = _effect.asSharedFlow()

    private val summaryRecords = combine(
        getRunningSummaryUseCase(),
        getRunningRecordsUseCase()
    ) { summary, records ->
        HomeSummaryRecords(summary = summary, records = records)
    }

    val uiState: StateFlow<HomeUiState> =
        combine(
            summaryRecords,
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
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = run {
                val initialCalendarMonth =
                    calendarCalculator.monthStateFromMillis(dateProvider.getToday())
                HomeUiState(
                    selectedDateState = SelectedDateState(
                        dateMillis = periodDateCalculator.startOfDayMillis(dateProvider.getToday())
                    ),
                    isCalendarVisible = false,
                    calendarMonthState = initialCalendarMonth,
                    calendarDays = calendarCalculator.buildCalendarDays(initialCalendarMonth)
                )
            }
        )

    fun onPeriodSelected(period: PeriodState) {
        periodState.value = period
    }

    fun onNavigatePreviousPeriod() {
        selectedDateState.value = SelectedDateState(
            dateMillis = periodDateCalculator.shiftDateByPeriod(
                selectedDateMillis = selectedDateState.value.dateMillis,
                period = periodState.value,
                step = -1
            )
        )
    }

    fun onNavigateNextPeriod() {
        selectedDateState.value = SelectedDateState(
            dateMillis = periodDateCalculator.shiftDateByPeriod(
                selectedDateMillis = selectedDateState.value.dateMillis,
                period = periodState.value,
                step = 1
            )
        )
    }

    fun onDateSelected(dateMillis: Long) {
        periodState.value = PeriodState.DAILY
        selectedDateState.value = SelectedDateState(
            dateMillis = periodDateCalculator.startOfDayMillis(dateMillis)
        )
        calendarMonthState.value = calendarCalculator.monthStateFromMillis(dateMillis)
        calendarVisibility.value = false
    }

    fun onCalendarOpen() {
        calendarVisibility.value = true
    }

    fun onCalendarDismiss() {
        calendarVisibility.value = false
    }

    fun onPreviousCalendarMonth() =
        calendarMonthState.update { current ->
            calendarCalculator.shiftMonth(current, -1)
        }

    fun onNextCalendarMonth() =
        calendarMonthState.update { current ->
            calendarCalculator.shiftMonth(current, 1)
        }

    fun onRecordClick() = emitEffect(HomeUiEffect.NavigateToRecord)

    fun onGoalClick() = emitEffect(HomeUiEffect.NavigateToGoal)

    fun onReminderClick() = emitEffect(HomeUiEffect.NavigateToReminder)

    private fun emitEffect(effect: HomeUiEffect) = viewModelScope.launch { _effect.emit(effect) }
}
