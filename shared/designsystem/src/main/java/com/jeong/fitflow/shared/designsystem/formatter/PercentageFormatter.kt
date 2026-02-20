package com.jeong.fitflow.shared.designsystem.formatter

import java.text.NumberFormat
import java.util.Locale

class PercentageFormatter(
    private val localeProvider: () -> Locale,
    private val numberFormatFactory: (Locale) -> NumberFormat
) {
    fun formatProgress(progress: Float, fractionDigits: Int, percentScale: Int): String {
        val locale = localeProvider()
        val formatter = numberFormatFactory(locale)
        formatter.minimumFractionDigits = fractionDigits
        formatter.maximumFractionDigits = fractionDigits
        return formatter.format(progress.toDouble() * percentScale)
    }
}
