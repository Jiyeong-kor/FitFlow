package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.ActivityCaloriesSegment
import com.jeong.runninggoaltracker.domain.model.CardioActivityType
import org.junit.Assert.assertEquals
import org.junit.Test

class EstimateActivityCaloriesUseCaseTest {

    private val useCase = EstimateActivityCaloriesUseCase()

    @Test
    fun `met mapping matches default table`() {
        assertEquals(9.3, useCase.metFor(CardioActivityType.Running), 0.0)
        assertEquals(3.5, useCase.metFor(CardioActivityType.Walking), 0.0)
        assertEquals(6.8, useCase.metFor(CardioActivityType.OnBicycle), 0.0)
    }

    @Test
    fun `other activity type contributes zero calories`() {
        val kcal = useCase.estimateSegmentCalories(
            activityType = CardioActivityType.Other,
            durationSeconds = 600,
            userWeightKg = 70.0
        )

        assertEquals(0.0, kcal, 0.0)
    }

    @Test
    fun `accumulates calories across activity segments`() {
        val calories = useCase(
            segments = listOf(
                ActivityCaloriesSegment(CardioActivityType.Running, durationSeconds = 600),
                ActivityCaloriesSegment(CardioActivityType.Walking, durationSeconds = 300),
                ActivityCaloriesSegment(CardioActivityType.OnBicycle, durationSeconds = 120)
            ),
            userWeightKg = 70.0
        )

        val expected =
            (10.0 * (9.3 * 3.5 * 70.0) / 200.0) +
                (5.0 * (3.5 * 3.5 * 70.0) / 200.0) +
                (2.0 * (6.8 * 3.5 * 70.0) / 200.0)

        assertEquals(expected, calories, 1e-9)
    }

    @Test
    fun `keeps double precision without rounding`() {
        val calories = useCase.estimateSegmentCalories(
            activityType = CardioActivityType.Walking,
            durationSeconds = 61,
            userWeightKg = 64.3
        )

        val expected = (61.0 / 60.0) * (3.5 * 3.5 * 64.3) / 200.0
        assertEquals(expected, calories, 1e-9)
    }
}
