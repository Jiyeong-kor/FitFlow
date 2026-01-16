package com.jeong.runninggoaltracker.shared.designsystem.formatter

import java.text.NumberFormat

object DistanceFormatter {

    fun formatDistanceKm(distanceKm: Double): String {
        val formatter = NumberFormat.getNumberInstance()
        formatter.minimumFractionDigits = 1
        formatter.maximumFractionDigits = 1
        return formatter.format(distanceKm)
    }
}
