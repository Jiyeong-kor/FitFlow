package com.jeong.runninggoaltracker.shared.designsystem.formatter

import android.content.Context
import com.jeong.runninggoaltracker.shared.designsystem.config.NumericResourceProvider
import java.text.NumberFormat

object DistanceFormatter {

    fun formatDistanceKm(context: Context, distanceKm: Double): String {
        val formatter = NumberFormat.getNumberInstance()
        val fractionDigits = NumericResourceProvider.distanceFractionDigits(context)
        formatter.minimumFractionDigits = fractionDigits
        formatter.maximumFractionDigits = fractionDigits
        return formatter.format(distanceKm)
    }
}
