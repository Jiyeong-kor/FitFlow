package com.jeong.fitflow.shared.designsystem.util

import com.jeong.fitflow.shared.designsystem.formatter.DistanceFormatter
import com.jeong.fitflow.shared.designsystem.formatter.PercentageFormatter
import java.text.NumberFormat
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class DateFormattersTest {

    @Test
    fun `거리 값을 지정된 자릿수로 포맷한다`() {
        val formatter = DistanceFormatter(
            localeProvider = { Locale.KOREA },
            numberFormatFactory = { locale ->
                NumberFormat.getNumberInstance(locale).apply { isGroupingUsed = false }
            }
        )

        val result = formatter.formatDistanceKm(distanceKm = 3.456, fractionDigits = 1)

        assertEquals("3.5", result)
    }

    @Test
    fun `진행률 값을 퍼센트로 포맷한다`() {
        val formatter = PercentageFormatter(
            localeProvider = { Locale.KOREA },
            numberFormatFactory = { locale ->
                NumberFormat.getNumberInstance(locale).apply { isGroupingUsed = false }
            }
        )

        val result = formatter.formatProgress(progress = 0.756f, fractionDigits = 0, percentScale = 100)

        assertEquals("76", result)
    }
}
