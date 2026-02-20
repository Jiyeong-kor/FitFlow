package com.jeong.fitflow.domain.util

import com.jeong.fitflow.domain.contract.RunningSummaryContract
import com.jeong.fitflow.domain.contract.RunningTimeContract
import com.jeong.fitflow.domain.model.RunningPeriodSummary
import com.jeong.fitflow.domain.model.RunningRecord
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
