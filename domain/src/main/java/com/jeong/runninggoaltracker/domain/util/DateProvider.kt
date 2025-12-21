package com.jeong.runninggoaltracker.domain.util

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface DateProvider {
    fun getTodayFlow(): Flow<LocalDate>
    fun getToday(): LocalDate
}
