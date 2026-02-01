package com.jeong.runninggoaltracker.feature.home.domain

import com.jeong.runninggoaltracker.domain.contract.DateTimeContract
import java.util.Calendar
import javax.inject.Inject

class HomeDateRangeCalculator @Inject constructor() {

    fun weekRange(selectedDateMillis: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
            firstDayOfWeek = DateTimeContract.WEEK_START_DAY
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, DateTimeContract.WEEK_END_OFFSET_DAYS)
        val end = calendar.timeInMillis
        return start to end
    }
}
