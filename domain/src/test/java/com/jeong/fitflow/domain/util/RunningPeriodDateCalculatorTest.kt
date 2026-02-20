package com.jeong.fitflow.domain.util

import com.jeong.fitflow.domain.contract.DateTimeContract
import com.jeong.fitflow.domain.contract.RunningTimeContract
import com.jeong.fitflow.domain.model.PeriodState
import com.jeong.fitflow.domain.model.RunningRecord
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RunningPeriodDateCalculatorTest {

    private val calculator = RunningPeriodDateCalculator()

    @Test
    fun `startOfDayMillis는 시각을 0으로 보정한다`() {
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 10, 15, 45, 30)
            set(Calendar.MILLISECOND, 999)
        }

        val startMillis = calculator.startOfDayMillis(calendar.timeInMillis)

        val expected = Calendar.getInstance().apply {
            timeInMillis = calendar.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        assertEquals(expected, startMillis)
    }

    @Test
    fun `shiftDateByPeriod는 기간별로 날짜를 이동한다`() {
        val base = Calendar.getInstance().apply {
            set(2024, Calendar.MARCH, 15, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val daily = calculator.shiftDateByPeriod(base, PeriodState.DAILY, -1)
        val expectedDaily = Calendar.getInstance().apply {
            timeInMillis = base
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val weekly = calculator.shiftDateByPeriod(base, PeriodState.WEEKLY, 1)
        val expectedWeekly = Calendar.getInstance().apply {
            timeInMillis = base
            add(Calendar.WEEK_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val monthly = calculator.shiftDateByPeriod(base, PeriodState.MONTHLY, 1)
        val expectedMonthly = Calendar.getInstance().apply {
            timeInMillis = base
            add(Calendar.MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        assertEquals(expectedDaily, daily)
        assertEquals(expectedWeekly, weekly)
        assertEquals(expectedMonthly, monthly)
    }

    @Test
    fun `shiftDateByPeriod는 시간 정보를 0으로 보정한다`() {
        val base = Calendar.getInstance().apply {
            set(2024, Calendar.MAY, 20, 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val shifted = calculator.shiftDateByPeriod(base, PeriodState.DAILY, 0)

        val expected = Calendar.getInstance().apply {
            timeInMillis = base
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        assertEquals(expected, shifted)
    }

    @Test
    fun `filterByPeriod는 일간 범위를 필터링한다`() {
        val selectedDate = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 5, 9, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val start = calculator.startOfDayMillis(selectedDate)
        val records = listOf(
            RunningRecord(id = 1, date = start, distanceKm = 1.0, durationMinutes = 10),
            RunningRecord(
                id = 2,
                date = start + RunningTimeContract.MILLIS_PER_DAY - 1,
                distanceKm = 2.0,
                durationMinutes = 20
            ),
            RunningRecord(
                id = 3,
                date = start + RunningTimeContract.MILLIS_PER_DAY,
                distanceKm = 3.0,
                durationMinutes = 30
            )
        )

        val filtered = calculator.filterByPeriod(records, PeriodState.DAILY, selectedDate)

        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.id != 3L })
    }

    @Test
    fun `filterByPeriod는 주간 범위를 필터링한다`() {
        val selectedDate = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 3, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val startOfWeek = Calendar.getInstance().apply {
            timeInMillis = selectedDate
            firstDayOfWeek = DateTimeContract.WEEK_START_DAY
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val endOfWeek =
            startOfWeek + (RunningTimeContract.MILLIS_PER_DAY * DateTimeContract.DAYS_IN_WEEK)
        val records = listOf(
            RunningRecord(id = 1, date = startOfWeek, distanceKm = 1.0, durationMinutes = 10),
            RunningRecord(
                id = 2,
                date = startOfWeek + RunningTimeContract.MILLIS_PER_DAY * 2,
                distanceKm = 2.0,
                durationMinutes = 20
            ),
            RunningRecord(id = 3, date = endOfWeek, distanceKm = 3.0, durationMinutes = 30)
        )

        val filtered = calculator.filterByPeriod(records, PeriodState.WEEKLY, selectedDate)

        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.date < endOfWeek })
    }

    @Test
    fun `filterByPeriod는 월간 범위를 필터링한다`() {
        val selectedDate = Calendar.getInstance().apply {
            set(2024, Calendar.APRIL, 18, 8, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val startOfMonth = Calendar.getInstance().apply {
            timeInMillis = selectedDate
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val endOfMonth = Calendar.getInstance().apply {
            timeInMillis = startOfMonth
            add(Calendar.MONTH, 1)
        }.timeInMillis
        val records = listOf(
            RunningRecord(id = 1, date = startOfMonth, distanceKm = 1.0, durationMinutes = 10),
            RunningRecord(
                id = 2,
                date = endOfMonth - RunningTimeContract.MILLIS_PER_DAY,
                distanceKm = 2.0,
                durationMinutes = 20
            ),
            RunningRecord(id = 3, date = endOfMonth, distanceKm = 3.0, durationMinutes = 30)
        )

        val filtered = calculator.filterByPeriod(records, PeriodState.MONTHLY, selectedDate)

        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.date < endOfMonth })
    }
}
