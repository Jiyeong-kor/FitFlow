package com.jeong.runninggoaltracker.domain.model.time

import java.util.Calendar

data class AppDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<AppDate> {

    companion object {
        fun now(): AppDate {
            val cal = Calendar.getInstance()
            return AppDate(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH) + 1,
                day = cal.get(Calendar.DAY_OF_MONTH)
            )
        }
    }

    fun isBefore(other: AppDate): Boolean {
        return this < other
    }

    fun with(targetDay: AppDayOfWeek): AppDate {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day)

        val calendarTargetDay = if (targetDay == AppDayOfWeek.SUNDAY) {
            Calendar.SUNDAY
        } else {
            targetDay.value + 1
        }

        cal.set(Calendar.DAY_OF_WEEK, calendarTargetDay)

        return AppDate(
            year = cal.get(Calendar.YEAR),
            month = cal.get(Calendar.MONTH) + 1,
            day = cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun compareTo(other: AppDate): Int {
        if (this.year != other.year) return this.year - other.year
        if (this.month != other.month) return this.month - other.month
        return this.day - other.day
    }
}
