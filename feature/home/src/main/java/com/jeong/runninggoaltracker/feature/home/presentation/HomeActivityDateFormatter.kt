package com.jeong.runninggoaltracker.feature.home.presentation

import java.text.SimpleDateFormat
import java.util.Locale

class HomeActivityDateFormatter {
    fun format(timestamp: Long, locale: Locale): String =
        SimpleDateFormat(patternFor(locale), locale).format(timestamp)

    private fun patternFor(locale: Locale): String =
        if (locale.language == Locale.KOREAN.language) {
            "M월 d일"
        } else {
            "MMM d"
        }
}
