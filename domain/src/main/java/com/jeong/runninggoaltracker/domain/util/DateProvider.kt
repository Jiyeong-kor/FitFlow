package com.jeong.runninggoaltracker.domain.util

import com.jeong.runninggoaltracker.domain.model.time.AppDate
import kotlinx.coroutines.flow.Flow

interface DateProvider {
    fun getTodayFlow(): Flow<AppDate>
    fun getToday(): AppDate
}
