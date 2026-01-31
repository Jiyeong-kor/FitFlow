package com.jeong.runninggoaltracker.shared.designsystem.formatter

import java.text.NumberFormat

object DistanceFormatter {

    fun formatDistanceKm(distanceKm: Double, fractionDigits: Int): String {
        val formatter = NumberFormat.getNumberInstance()
        formatter.minimumFractionDigits = fractionDigits
        formatter.maximumFractionDigits = fractionDigits
        return formatter.format(distanceKm)
    }
}
