package com.jeong.fitflow.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.fitflow.domain.model.PeriodState
import com.jeong.fitflow.domain.util.DateProvider
import com.jeong.fitflow.domain.util.RunningPeriodDateCalculator
import com.jeong.fitflow.feature.home.domain.HomeCalendarCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    dateProvider: DateProvider,
    private val periodDateCalculator: RunningPeriodDateCalculator,
    private val calendarCalculator: HomeCalendarCalculator,
    stateHolder: HomeStateHolder
) : ViewModel() {

    private val initialSelectedDateState = SelectedDateState(
        dateMillis = periodDateCalculator.startOfDayMillis(dateProvider.getToday())
    )
    private val initialCalendarMonthState =
        calendarCalculator.monthStateFromMillis(dateProvider.getToday())

    private val periodState = MutableStateFlow(PeriodState.DAILY)
    private val selectedDateState = MutableStateFlow(initialSelectedDateState)
    private val calendarVisibility = MutableStateFlow(false)
    private val calendarMonthState = MutableStateFlow(initialCalendarMonthState)

    val uiState: StateFlow<HomeUiState> =
        stateHolder.uiState(
            periodState = periodState,
            selectedDateState = selectedDateState,
            calendarVisibility = calendarVisibility,
            calendarMonthState = calendarMonthState,
            initialState = stateHolder.initialState(
                period = PeriodState.DAILY,
                selectedDateState = initialSelectedDateState,
                isCalendarVisible = false,
                calendarMonthState = initialCalendarMonthState
            ),
            scope = viewModelScope
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

    fun onPreviousCalendarMonth() {
        calendarMonthState.update { current ->
            calendarCalculator.shiftMonth(current, -1)
        }
    }

    fun onNextCalendarMonth() {
        calendarMonthState.update { current ->
            calendarCalculator.shiftMonth(current, 1)
        }
    }
}
