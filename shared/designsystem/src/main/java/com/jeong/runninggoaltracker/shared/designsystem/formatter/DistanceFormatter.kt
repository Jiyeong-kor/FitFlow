package com.jeong.runninggoaltracker.shared.designsystem.formatter

import java.text.NumberFormat
import java.util.Locale

class DistanceFormatter(
    private val localeProvider: () -> Locale,
    private val numberFormatFactory: (Locale) -> NumberFormat
) {
    fun formatDistanceKm(distanceKm: Double, fractionDigits: Int): String =
        numberFormatFactory(localeProvider()).apply {
            minimumFractionDigits = fractionDigits
            maximumFractionDigits = fractionDigits
        }.format(distanceKm)
}
