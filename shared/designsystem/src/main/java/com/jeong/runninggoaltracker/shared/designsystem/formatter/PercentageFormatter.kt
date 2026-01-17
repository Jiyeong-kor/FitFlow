package com.jeong.runninggoaltracker.shared.designsystem.formatter

import android.content.Context
import com.jeong.runninggoaltracker.shared.designsystem.config.NumericResourceProvider
import java.text.NumberFormat

object PercentageFormatter {
    fun formatProgress(context: Context, progress: Float): String {
        val formatter = NumberFormat.getNumberInstance()
        val fractionDigits = NumericResourceProvider.percentageFractionDigits(context)
        val percentScale = NumericResourceProvider.percentageScale(context)
        formatter.minimumFractionDigits = fractionDigits
        formatter.maximumFractionDigits = fractionDigits
        return formatter.format(progress.toDouble() * percentScale)
    }
}
