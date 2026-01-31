package com.jeong.runninggoaltracker.shared.designsystem.formatter

import java.text.NumberFormat

object PercentageFormatter {
    fun formatProgress(progress: Float, fractionDigits: Int, percentScale: Int): String {
        val formatter = NumberFormat.getNumberInstance()
        formatter.minimumFractionDigits = fractionDigits
        formatter.maximumFractionDigits = fractionDigits
        return formatter.format(progress.toDouble() * percentScale)
    }
}
