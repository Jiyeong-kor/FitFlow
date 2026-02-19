package com.jeong.runninggoaltracker.feature.home.presentation

import com.jeong.runninggoaltracker.domain.model.PeriodState
import com.jeong.runninggoaltracker.feature.home.R
import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeUiFormatterTest {
    @Test
    fun periodLabelTextFormatsDailyRangeAndMonth() {
        val originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        try {
            val locale = Locale.US
            val selectedDateMillis = 0L
            val weeklyRange = HomeWeeklyRange(
                startMillis = 0L,
                endMillis = 86_400_000L
            )
            val dayPattern = "yyyy-MM-dd"
            val monthPattern = "yyyy-MM"
            val rangePattern = "yyyy-MM-dd"

            val dailyText = HomeUiFormatter.periodLabelText(
                periodState = PeriodState.DAILY,
                selectedDateMillis = selectedDateMillis,
                weeklyRange = weeklyRange,
                dayPattern = dayPattern,
                monthPattern = monthPattern,
                rangePattern = rangePattern,
                locale = locale
            )

            val weeklyText = HomeUiFormatter.periodLabelText(
                periodState = PeriodState.WEEKLY,
                selectedDateMillis = selectedDateMillis,
                weeklyRange = weeklyRange,
                dayPattern = dayPattern,
                monthPattern = monthPattern,
                rangePattern = rangePattern,
                locale = locale
            )

            val monthlyText = HomeUiFormatter.periodLabelText(
                periodState = PeriodState.MONTHLY,
                selectedDateMillis = selectedDateMillis,
                weeklyRange = weeklyRange,
                dayPattern = dayPattern,
                monthPattern = monthPattern,
                rangePattern = rangePattern,
                locale = locale
            )

            assertEquals(R.string.home_period_daily_format, dailyText.resId)
            assertEquals(listOf("1970-01-01"), dailyText.formatArgs)
            assertEquals(R.string.home_period_weekly_range_format, weeklyText.resId)
            assertEquals(listOf("1970-01-01", "1970-01-02"), weeklyText.formatArgs)
            assertEquals(R.string.home_period_monthly_format, monthlyText.resId)
            assertEquals(listOf("1970-01"), monthlyText.formatArgs)
        } finally {
            TimeZone.setDefault(originalTimeZone)
        }
    }

    @Test
    fun dateTextFormatsMonthAndRange() {
        val originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        try {
            val locale = Locale.US
            val rangePattern = "yyyy-MM-dd"
            val monthPattern = "yyyy-MM"

            val activityText = HomeUiFormatter.activityDateText(
                timestampMillis = 0L,
                rangePattern = rangePattern,
                locale = locale
            )
            val monthText = HomeUiFormatter.yearMonthText(
                year = 2024,
                month = 0,
                monthPattern = monthPattern,
                locale = locale
            )

            assertEquals("1970-01-01", activityText)
            assertEquals("2024-01", monthText)
        } finally {
            TimeZone.setDefault(originalTimeZone)
        }
    }
}
