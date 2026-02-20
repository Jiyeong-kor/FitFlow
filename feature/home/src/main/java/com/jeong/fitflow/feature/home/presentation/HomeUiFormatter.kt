package com.jeong.fitflow.feature.home.presentation

import com.jeong.fitflow.domain.model.PeriodState
import com.jeong.fitflow.feature.home.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HomeTextSpec(
    val resId: Int,
    val formatArgs: List<Any> = emptyList()
)

object HomeUiFormatter {
    fun periodLabelText(
        periodState: PeriodState,
        selectedDateMillis: Long,
        weeklyRange: HomeWeeklyRange,
        dayPattern: String,
        monthPattern: String,
        rangePattern: String,
        locale: Locale
    ): HomeTextSpec {
        val dayFormatter = SimpleDateFormat(dayPattern, locale)
        val monthFormatter = SimpleDateFormat(monthPattern, locale)
        val rangeFormatter = SimpleDateFormat(rangePattern, locale)
        return when (periodState) {
            PeriodState.DAILY -> HomeTextSpec(
                resId = R.string.home_period_daily_format,
                formatArgs = listOf(dayFormatter.format(selectedDateMillis))
            )

            PeriodState.WEEKLY -> HomeTextSpec(
                resId = R.string.home_period_weekly_range_format,
                formatArgs = listOf(
                    rangeFormatter.format(weeklyRange.startMillis),
                    rangeFormatter.format(weeklyRange.endMillis)
                )
            )

            PeriodState.MONTHLY -> HomeTextSpec(
                resId = R.string.home_period_monthly_format,
                formatArgs = listOf(monthFormatter.format(selectedDateMillis))
            )
        }
    }

    fun activityDateText(
        timestampMillis: Long,
        rangePattern: String,
        locale: Locale
    ): String =
        SimpleDateFormat(rangePattern, locale).format(timestampMillis)

    fun yearMonthText(
        year: Int,
        month: Int,
        monthPattern: String,
        locale: Locale
    ): String =
        Calendar.getInstance().run {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            SimpleDateFormat(monthPattern, locale).format(timeInMillis)
        }
}
