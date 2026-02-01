package com.jeong.runninggoaltracker.shared.designsystem.formatter

import java.text.NumberFormat
import java.util.Locale

class DistanceFormatter(
    private val localeProvider: () -> Locale,
    private val numberFormatFactory: (Locale) -> NumberFormat
) {
    fun formatDistanceKm(distanceKm: Double, fractionDigits: Int): String {
        val locale = localeProvider()
        val formatter = numberFormatFactory(locale)
        formatter.minimumFractionDigits = fractionDigits
        formatter.maximumFractionDigits = fractionDigits
        return formatter.format(distanceKm)
    }
}
