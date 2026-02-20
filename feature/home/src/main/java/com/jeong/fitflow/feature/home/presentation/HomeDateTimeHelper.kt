package com.jeong.fitflow.feature.home.presentation

import android.os.Build
import com.jeong.fitflow.feature.home.contract.HomeDateTimeContract
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

internal data class HomeActivityDateInfo(
    val month: Int,
    val day: Int,
    val dayOfWeek: Int
)

internal fun extractActivityDateInfo(timestamp: Long): HomeActivityDateInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val localDate = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        HomeActivityDateInfo(
            month = localDate.monthValue,
            day = localDate.dayOfMonth,
            dayOfWeek = localDate.dayOfWeek.value
        )
    } else {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        HomeActivityDateInfo(
            month = calendar.get(Calendar.MONTH) + HomeDateTimeContract.MONTH_OFFSET,
            day = calendar.get(Calendar.DAY_OF_MONTH),
            dayOfWeek = calendarDayOfWeekToValue(calendar.get(Calendar.DAY_OF_WEEK))
        )
    }

private fun calendarDayOfWeekToValue(dayOfWeek: Int): Int =
    when (dayOfWeek) {
        Calendar.MONDAY -> HomeDateTimeContract.DAY_OF_WEEK_MON
        Calendar.TUESDAY -> HomeDateTimeContract.DAY_OF_WEEK_TUE
        Calendar.WEDNESDAY -> HomeDateTimeContract.DAY_OF_WEEK_WED
        Calendar.THURSDAY -> HomeDateTimeContract.DAY_OF_WEEK_THU
        Calendar.FRIDAY -> HomeDateTimeContract.DAY_OF_WEEK_FRI
        Calendar.SATURDAY -> HomeDateTimeContract.DAY_OF_WEEK_SAT
        else -> HomeDateTimeContract.DAY_OF_WEEK_SUN
    }
