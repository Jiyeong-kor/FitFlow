package com.jeong.runninggoaltracker.shared.designsystem.formatter

import java.text.NumberFormat

object PercentageFormatter {
    private const val FRACTION_DIGITS = 0
    private const val PERCENT_SCALE = 100

    fun formatProgress(progress: Float): String {
        val formatter = NumberFormat.getNumberInstance()
        formatter.minimumFractionDigits = FRACTION_DIGITS
        formatter.maximumFractionDigits = FRACTION_DIGITS
        return formatter.format(progress.toDouble() * PERCENT_SCALE)
    }
}
