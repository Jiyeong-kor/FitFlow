package com.jeong.fitflow.shared.designsystem.formatter

import java.text.NumberFormat
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class DistanceFormatterTest {

    @Test
    fun `거리 값과 소수점 자릿수를 반영해 포맷한다`() {
        val formatter = DistanceFormatter(
            localeProvider = { Locale.US },
            numberFormatFactory = { locale ->
                NumberFormat.getNumberInstance(locale).apply {
                    isGroupingUsed = false
                }
            }
        )

        val result = formatter.formatDistanceKm(distanceKm = 1.5, fractionDigits = 2)

        assertEquals("1.50", result)
    }
}
