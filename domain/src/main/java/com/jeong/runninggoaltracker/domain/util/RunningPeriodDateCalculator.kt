package com.jeong.runninggoaltracker.domain.util

import com.jeong.runninggoaltracker.domain.contract.DateTimeContract
import com.jeong.runninggoaltracker.domain.contract.RunningTimeContract
import com.jeong.runninggoaltracker.domain.model.PeriodState
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import java.util.Calendar
import javax.inject.Inject

class RunningPeriodDateCalculator @Inject constructor() {

    fun startOfDayMillis(dateMillis: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = dateMillis
            resetToStartOfDay(this)
        }.timeInMillis

    fun shiftDateByPeriod(
        selectedDateMillis: Long,
        period: PeriodState,
        step: Int
    ): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
            resetToStartOfDay(this)
        }
        when (period) {
            PeriodState.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, step)
            PeriodState.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, step)
            PeriodState.MONTHLY -> calendar.add(Calendar.MONTH, step)
        }
        return calendar.timeInMillis
    }

    fun isDateInPeriod(
        dateMillis: Long,
        period: PeriodState,
        selectedDateMillis: Long
    ): Boolean {
        val range = when (period) {
            PeriodState.DAILY -> dateRangeForDay(selectedDateMillis)
            PeriodState.WEEKLY -> dateRangeForWeek(selectedDateMillis)
            PeriodState.MONTHLY -> dateRangeForMonth(selectedDateMillis)
        }
        return dateMillis in range.first until range.second
    }

    fun filterByPeriod(
        records: List<RunningRecord>,
        period: PeriodState,
        selectedDateMillis: Long
    ): List<RunningRecord> {
        val range = when (period) {
            PeriodState.DAILY -> dateRangeForDay(selectedDateMillis)
            PeriodState.WEEKLY -> dateRangeForWeek(selectedDateMillis)
            PeriodState.MONTHLY -> dateRangeForMonth(selectedDateMillis)
        }
        return records.filter { record -> record.date in range.first until range.second }
    }

    private fun dateRangeForDay(selectedDateMillis: Long): Pair<Long, Long> {
        val start = startOfDayMillis(selectedDateMillis)
        val end = start + RunningTimeContract.MILLIS_PER_DAY
        return start to end
    }

    private fun dateRangeForWeek(selectedDateMillis: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
            firstDayOfWeek = DateTimeContract.WEEK_START_DAY
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            resetToStartOfDay(this)
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
            resetToStartOfDay(this)
        }
        val start = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1)
        val end = calendar.timeInMillis
        return start to end
    }

    private fun resetToStartOfDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }
}
