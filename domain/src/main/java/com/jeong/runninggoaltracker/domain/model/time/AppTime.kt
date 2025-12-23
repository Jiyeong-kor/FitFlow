package com.jeong.runninggoaltracker.domain.model.time

import java.util.Calendar
import java.util.Locale

data class AppTime(
    val hour: Int,
    val minute: Int,
    val second: Int = 0
) {
    companion object {
        fun now(): AppTime {
            val cal = Calendar.getInstance()
            return AppTime(
                hour = cal.get(Calendar.HOUR_OF_DAY),
                minute = cal.get(Calendar.MINUTE),
                second = cal.get(Calendar.SECOND)
            )
        }
    }

    fun format(pattern: String): String {
        return pattern
            .replace("HH", String.format(Locale.US, "%02d", hour))
            .replace("mm", String.format(Locale.US, "%02d", minute))
            .replace("ss", String.format(Locale.US, "%02d", second))
    }
}
