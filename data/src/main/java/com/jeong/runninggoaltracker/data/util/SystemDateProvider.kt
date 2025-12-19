package com.jeong.runninggoaltracker.data.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.jeong.runninggoaltracker.domain.util.DateProvider
import java.time.LocalDate

class SystemDateProvider : DateProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getToday(): LocalDate = LocalDate.now()
}
