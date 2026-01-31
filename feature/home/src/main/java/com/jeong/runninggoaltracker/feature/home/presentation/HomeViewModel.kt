package com.jeong.runninggoaltracker.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.PeriodState
import com.jeong.runninggoaltracker.domain.usecase.GetRunningSummaryUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRecordsUseCase
import com.jeong.runninggoaltracker.domain.util.DateProvider
import com.jeong.runninggoaltracker.domain.util.RunningPeriodDateCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val periodState: PeriodState = PeriodState.DAILY,
    val selectedDateState: SelectedDateState,
    val isCalendarVisible: Boolean = false,
    val summary: HomeSummaryUiState = HomeSummaryUiState(),
    val activityLogs: List<HomeWorkoutLogUiModel> = emptyList(),
    val weeklyGoalKm: Double? = null
)

sealed interface HomeUiEffect {
    data object NavigateToRecord : HomeUiEffect
    data object NavigateToGoal : HomeUiEffect
    data object NavigateToReminder : HomeUiEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    getRunningSummaryUseCase: GetRunningSummaryUseCase,
    getRunningRecordsUseCase: GetRunningRecordsUseCase,
    dateProvider: DateProvider,
    private val periodDateCalculator: RunningPeriodDateCalculator,
    private val uiStateMapper: HomeUiStateMapper
) : ViewModel() {

    private val periodState = MutableStateFlow(PeriodState.DAILY)
    private val selectedDateState = MutableStateFlow(
        SelectedDateState(dateMillis = periodDateCalculator.startOfDayMillis(dateProvider.getToday()))
    )
    private val calendarVisibility = MutableStateFlow(false)

    private val _effect = MutableSharedFlow<HomeUiEffect>()
    val effect = _effect.asSharedFlow()

    val uiState: StateFlow<HomeUiState> =
        combine(
            getRunningSummaryUseCase(),
            getRunningRecordsUseCase(),
            periodState,
            selectedDateState,
            calendarVisibility
        ) { summary, records, period, selectedDate, isCalendarVisible ->
            uiStateMapper.map(
                summary = summary,
                records = records,
                period = period,
                selectedDateState = selectedDate,
                isCalendarVisible = isCalendarVisible
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HomeUiState(
                selectedDateState = SelectedDateState(
                    dateMillis = periodDateCalculator.startOfDayMillis(dateProvider.getToday())
                ),
                isCalendarVisible = false
            )
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
        calendarVisibility.value = false
    }

    fun onCalendarOpen() {
        calendarVisibility.value = true
    }

    fun onCalendarDismiss() {
        calendarVisibility.value = false
    }

    fun onRecordClick() = emitEffect(HomeUiEffect.NavigateToRecord)

    fun onGoalClick() = emitEffect(HomeUiEffect.NavigateToGoal)

    fun onReminderClick() = emitEffect(HomeUiEffect.NavigateToReminder)

    private fun emitEffect(effect: HomeUiEffect) = viewModelScope.launch { _effect.emit(effect) }
}
