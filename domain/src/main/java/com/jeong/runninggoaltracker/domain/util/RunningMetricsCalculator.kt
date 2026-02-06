package com.jeong.runninggoaltracker.domain.util

import com.jeong.runninggoaltracker.domain.contract.RunningTimeContract
import com.jeong.runninggoaltracker.domain.model.RunningElapsedTime
import com.jeong.runninggoaltracker.domain.model.RunningPace
import javax.inject.Inject

class RunningMetricsCalculator @Inject constructor() {

    fun calculateElapsedTime(elapsedMillis: Long): RunningElapsedTime {
        val totalSeconds = elapsedMillis / RunningTimeContract.MILLIS_PER_SECOND
        val hours = totalSeconds / RunningTimeContract.SECONDS_PER_HOUR
        val minutes =
            (totalSeconds / RunningTimeContract.SECONDS_PER_MINUTE) %
                RunningTimeContract.MINUTES_PER_HOUR
        val seconds = totalSeconds % RunningTimeContract.SECONDS_PER_MINUTE
        return RunningElapsedTime(
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            shouldShowHours = hours > RunningTimeContract.ZERO_LONG
        )
    }

    fun calculatePace(distanceKm: Double, elapsedMillis: Long): RunningPace {
        if (!isPaceAvailable(distanceKm, elapsedMillis)) {
            return RunningPace(
                minutes = 0,
                seconds = 0,
                isAvailable = false
            )
        }
        val totalSeconds = elapsedMillis / RunningTimeContract.MILLIS_PER_SECOND
        val secondsPerKm = totalSeconds.toDouble() / distanceKm
        val minutes = (secondsPerKm / RunningTimeContract.SECONDS_PER_MINUTE).toInt()
        val seconds = (secondsPerKm % RunningTimeContract.SECONDS_PER_MINUTE).toInt()
        return RunningPace(
            minutes = minutes,
            seconds = seconds,
            isAvailable = true
        )
    }

    private fun isPaceAvailable(distanceKm: Double, elapsedMillis: Long): Boolean =
        distanceKm > RunningTimeContract.ZERO_DOUBLE &&
            elapsedMillis > RunningTimeContract.ZERO_LONG
}
