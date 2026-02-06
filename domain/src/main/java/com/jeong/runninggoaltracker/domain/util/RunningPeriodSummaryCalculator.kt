package com.jeong.runninggoaltracker.domain.util

import com.jeong.runninggoaltracker.domain.contract.RunningSummaryContract
import com.jeong.runninggoaltracker.domain.contract.RunningTimeContract
import com.jeong.runninggoaltracker.domain.model.RunningPeriodSummary
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import kotlin.math.roundToInt
import javax.inject.Inject

class RunningPeriodSummaryCalculator @Inject constructor(
    private val metricsCalculator: RunningMetricsCalculator
) {

    fun calculate(records: List<RunningRecord>): RunningPeriodSummary {
        val totalDistance = records.sumOf { it.distanceKm }
        val totalDurationMinutes = records.sumOf { it.durationMinutes }
        val totalDurationMillis = totalDurationMinutes * RunningTimeContract.MILLIS_PER_MINUTE
        val pace = metricsCalculator.calculatePace(totalDistance, totalDurationMillis)
        val calories = calculateCalories(totalDistance)
        return RunningPeriodSummary(
            totalDistanceKm = totalDistance,
            totalCalories = calories,
            totalDurationMinutes = totalDurationMinutes,
            averagePace = pace
        )
    }

    private fun calculateCalories(distanceKm: Double): Int =
        (distanceKm * RunningSummaryContract.CALORIES_PER_KM).roundToInt()
}
