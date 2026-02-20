package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.ActivityCaloriesSegment
import com.jeong.fitflow.domain.model.CardioActivityType
import javax.inject.Inject

class EstimateActivityCaloriesUseCase @Inject constructor() {

    operator fun invoke(
        segments: List<ActivityCaloriesSegment>,
        userWeightKg: Double
    ): Double {
        if (userWeightKg <= 0.0) return 0.0
        return segments.sumOf { segment ->
            estimateSegmentCalories(
                activityType = segment.activityType,
                durationSeconds = segment.durationSeconds,
                userWeightKg = userWeightKg
            )
        }
    }

    fun estimateSegmentCalories(
        activityType: CardioActivityType,
        durationSeconds: Long,
        userWeightKg: Double
    ): Double {
        if (durationSeconds <= 0L || userWeightKg <= 0.0) return 0.0
        val met = metFor(activityType)
        if (met == 0.0) return 0.0
        val minutes = durationSeconds / 60.0
        return minutes * (met * 3.5 * userWeightKg) / 200.0
    }

    fun metFor(activityType: CardioActivityType): Double = when (activityType) {
        CardioActivityType.Running -> 9.3
        CardioActivityType.Walking -> 3.5
        CardioActivityType.OnBicycle -> 6.8
        CardioActivityType.Other -> 0.0
    }
}
