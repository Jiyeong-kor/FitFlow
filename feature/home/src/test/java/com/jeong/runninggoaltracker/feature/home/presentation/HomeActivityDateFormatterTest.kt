package com.jeong.runninggoaltracker.feature.home.presentation

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class HomeActivityDateFormatterTest {
    private val formatter = HomeActivityDateFormatter()

    @Test
    fun `한국어 로케일은 월일 형식으로 포맷한다`() {
        val timestamp = timestampOf(2025, Calendar.JANUARY, 2)

        val result = formatter.format(timestamp, Locale.KOREAN)

        assertEquals("1월 2일", result)
    }

    @Test
    fun `영어 로케일은 약식 월 표기 형식으로 포맷한다`() {
        val timestamp = timestampOf(2025, Calendar.JANUARY, 2)

        val result = formatter.format(timestamp, Locale.US)

        assertEquals("Jan 2", result)
    }

    private fun timestampOf(year: Int, month: Int, dayOfMonth: Int): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US)
        calendar.set(year, month, dayOfMonth, 12, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
