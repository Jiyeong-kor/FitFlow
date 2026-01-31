package com.jeong.runninggoaltracker.shared.designsystem.formatter

import java.text.NumberFormat
import java.util.Locale

class DistanceFormatter(
    private val localeProvider: () -> Locale,
    private val numberFormatFactory: (Locale) -> NumberFormat
) {
    private var cachedLocale: Locale? = null
    private var cachedFormatter: NumberFormat? = null

    fun formatDistanceKm(distanceKm: Double, fractionDigits: Int): String {
        val locale = localeProvider()
        val formatter = cachedFormatter?.takeIf { cachedLocale == locale }
            ?: numberFormatFactory(locale).also {
                cachedLocale = locale
                cachedFormatter = it
            }
        formatter.minimumFractionDigits = fractionDigits
        formatter.maximumFractionDigits = fractionDigits
        return formatter.format(distanceKm)
    }
}
