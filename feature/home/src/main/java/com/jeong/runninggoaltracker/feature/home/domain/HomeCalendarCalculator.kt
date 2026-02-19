package com.jeong.runninggoaltracker.feature.home.domain

import com.jeong.runninggoaltracker.domain.contract.DateTimeContract
import java.util.Calendar
import javax.inject.Inject

data class CalendarMonthState(
    val year: Int,
    val month: Int
)

data class CalendarDay(
    val dayOfMonth: Int,
    val timestampMillis: Long,
    val hasActivity: Boolean = false
) {
    fun isSameDay(otherMillis: Long): Boolean =
        Calendar.getInstance().apply { timeInMillis = timestampMillis }
            .let { calendar ->
                val other = Calendar.getInstance().apply { timeInMillis = otherMillis }
                calendar.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                        calendar.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
            }
}

class HomeCalendarCalculator @Inject constructor() {

    fun monthStateFromMillis(dateMillis: Long): CalendarMonthState {
        val calendar = Calendar.getInstance().apply { timeInMillis = dateMillis }
        return CalendarMonthState(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH)
        )
    }

    fun shiftMonth(state: CalendarMonthState, offset: Int): CalendarMonthState {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, state.year)
            set(Calendar.MONTH, state.month)
            add(Calendar.MONTH, offset)
        }
        return CalendarMonthState(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH)
        )
    }

    fun buildCalendarDays(
        state: CalendarMonthState,
        activityDayStarts: Set<Long> = emptySet()
    ): List<CalendarDay?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, state.year)
            set(Calendar.MONTH, state.month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val offset =
            (firstDayOfWeek - DateTimeContract.WEEK_START_DAY).let {
                if (it < 0) it + DateTimeContract.DAYS_IN_WEEK else it
            }
        val totalCells = offset + daysInMonth
        return buildList {
            repeat(offset) { add(null) }
            for (day in 1..daysInMonth) {
                calendar.set(Calendar.DAY_OF_MONTH, day)
                val dayStart = calendar.timeInMillis
                add(
                    CalendarDay(
                        dayOfMonth = day,
                        timestampMillis = dayStart,
                        hasActivity = activityDayStarts.contains(dayStart)
                    )
                )
            }
            while (size < totalCells) {
                add(null)
            }
        }
    }
}
