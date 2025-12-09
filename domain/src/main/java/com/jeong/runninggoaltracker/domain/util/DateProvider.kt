package com.jeong.runninggoaltracker.domain.util

import java.time.LocalDate

interface DateProvider {
    fun getToday(): LocalDate
}

class SystemDateProvider : DateProvider {
    override fun getToday(): LocalDate = LocalDate.now()
}
