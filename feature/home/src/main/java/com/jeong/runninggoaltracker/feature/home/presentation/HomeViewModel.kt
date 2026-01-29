package com.jeong.runninggoaltracker.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.contract.DateTimeContract
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.usecase.GetRunningSummaryUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRecordsUseCase
import com.jeong.runninggoaltracker.domain.util.DateProvider
import com.jeong.runninggoaltracker.feature.home.R
import com.jeong.runninggoaltracker.feature.home.contract.HOME_ZERO_DOUBLE
import com.jeong.runninggoaltracker.feature.home.contract.HOME_ZERO_INT
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
import java.util.Calendar
import kotlin.math.floor
import kotlin.math.roundToInt

data class HomeUiState(
    val periodState: PeriodState = PeriodState.DAILY,
    val selectedDateState: SelectedDateState,
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
    dateProvider: DateProvider
) : ViewModel() {

    private val periodState = MutableStateFlow(PeriodState.DAILY)
    private val selectedDateState = MutableStateFlow(
        SelectedDateState(dateMillis = startOfDayMillis(dateProvider.getToday()))
    )

    private val _effect = MutableSharedFlow<HomeUiEffect>()
    val effect = _effect.asSharedFlow()

    val uiState: StateFlow<HomeUiState> =
        combine(
            getRunningSummaryUseCase(),
            getRunningRecordsUseCase(),
            periodState,
            selectedDateState
        ) { summary, records, period, selectedDate ->
            val filteredRecords = records.filterByPeriod(period, selectedDate.dateMillis)
            HomeUiState(
                periodState = period,
                selectedDateState = selectedDate,
                summary = buildSummary(filteredRecords),
                activityLogs = filteredRecords.map { record ->
                    HomeWorkoutLogUiModel(
                        id = record.id,
                        timestamp = record.date,
                        distanceKm = record.distanceKm,
                        repCount = 0,
                        durationMinutes = record.durationMinutes,
                        type = HomeWorkoutType.RUNNING,
                        typeLabelResId = R.string.activity_running
                    )
                },
                weeklyGoalKm = summary.weeklyGoalKm
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HomeUiState(
                selectedDateState = SelectedDateState(dateMillis = startOfDayMillis(dateProvider.getToday()))
            )
        )

    fun onPeriodSelected(period: PeriodState) {
        periodState.value = period
    }

    fun onNavigatePreviousPeriod() {
        selectedDateState.value = SelectedDateState(
            dateMillis = shiftDateByPeriod(
                selectedDateState.value.dateMillis,
                periodState.value,
                -1
            )
        )
    }

    fun onNavigateNextPeriod() {
        selectedDateState.value = SelectedDateState(
            dateMillis = shiftDateByPeriod(selectedDateState.value.dateMillis, periodState.value, 1)
        )
    }

    fun onDateSelected(dateMillis: Long) {
        periodState.value = PeriodState.DAILY
        selectedDateState.value = SelectedDateState(dateMillis = startOfDayMillis(dateMillis))
    }

    fun onRecordClick() = emitEffect(HomeUiEffect.NavigateToRecord)

    fun onGoalClick() = emitEffect(HomeUiEffect.NavigateToGoal)

    fun onReminderClick() = emitEffect(HomeUiEffect.NavigateToReminder)

    private fun emitEffect(effect: HomeUiEffect) = viewModelScope.launch { _effect.emit(effect) }

    private fun buildSummary(records: List<RunningRecord>): HomeSummaryUiState {
        val totalDistance = records.sumOf { it.distanceKm }
        val totalDurationMinutes = records.sumOf { it.durationMinutes }
        val calories = (totalDistance * HOME_CALORIES_PER_KM).roundToInt()
        val averagePace = calculateAveragePace(totalDistance, totalDurationMinutes)
        return HomeSummaryUiState(
            totalDistanceKm = totalDistance,
            totalCalories = calories,
            totalDurationMinutes = totalDurationMinutes,
            averagePace = averagePace
        )
    }

    private fun calculateAveragePace(
        totalDistanceKm: Double,
        totalDurationMinutes: Int
    ): HomePaceUiState {
        if (totalDistanceKm <= HOME_ZERO_DOUBLE || totalDurationMinutes <= HOME_ZERO_INT) {
            return HomePaceUiState()
        }
        val totalMinutes = totalDurationMinutes.toDouble()
        val paceMinutesTotal = totalMinutes / totalDistanceKm
        val minutesPart = floor(paceMinutesTotal).toInt()
        val rawSeconds = ((paceMinutesTotal - minutesPart) * HOME_SECONDS_PER_MINUTE).roundToInt()
        val normalizedMinutes = minutesPart + (rawSeconds / HOME_SECONDS_PER_MINUTE)
        val normalizedSeconds = rawSeconds % HOME_SECONDS_PER_MINUTE
        return HomePaceUiState(
            minutes = normalizedMinutes,
            seconds = normalizedSeconds,
            isAvailable = true
        )
    }

    private fun List<RunningRecord>.filterByPeriod(
        period: PeriodState,
        selectedDateMillis: Long
    ): List<RunningRecord> {
        val range = when (period) {
            PeriodState.DAILY -> dateRangeForDay(selectedDateMillis)
            PeriodState.WEEKLY -> dateRangeForWeek(selectedDateMillis)
            PeriodState.MONTHLY -> dateRangeForMonth(selectedDateMillis)
        }
        return filter { record -> record.date in range.first until range.second }
    }

    private fun dateRangeForDay(selectedDateMillis: Long): Pair<Long, Long> {
        val start = startOfDayMillis(selectedDateMillis)
        val end = start + HOME_MILLIS_PER_DAY
        return start to end
    }

    private fun dateRangeForWeek(selectedDateMillis: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
            firstDayOfWeek = DateTimeContract.WEEK_START_DAY
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, DateTimeContract.DAYS_IN_WEEK)
        val end = calendar.timeInMillis
        return start to end
    }

    private fun dateRangeForMonth(selectedDateMillis: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1)
        val end = calendar.timeInMillis
        return start to end
    }

    private fun shiftDateByPeriod(
        selectedDateMillis: Long,
        period: PeriodState,
        step: Int
    ): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        when (period) {
            PeriodState.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, step)
            PeriodState.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, step)
            PeriodState.MONTHLY -> calendar.add(Calendar.MONTH, step)
        }
        return calendar.timeInMillis
    }

    private fun startOfDayMillis(dateMillis: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}

private const val HOME_CALORIES_PER_KM = 60
private const val HOME_SECONDS_PER_MINUTE = 60
private const val HOME_MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
