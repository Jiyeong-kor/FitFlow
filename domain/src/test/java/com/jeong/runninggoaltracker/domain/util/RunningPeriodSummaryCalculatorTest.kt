package com.jeong.runninggoaltracker.domain.util

import com.jeong.runninggoaltracker.domain.model.RunningRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RunningPeriodSummaryCalculatorTest {

    private val calculator = RunningPeriodSummaryCalculator(RunningMetricsCalculator())

    @Test
    fun `calculate returns zero summary for empty records`() {
        val summary = calculator.calculate(emptyList())

        assertEquals(0.0, summary.totalDistanceKm, 0.0)
        assertEquals(0, summary.totalCalories)
        assertEquals(0, summary.totalDurationMinutes)
        assertFalse(summary.averagePace.isAvailable)
    }

    @Test
    fun `calculate aggregates totals and pace`() {
        val records = listOf(
            RunningRecord(id = 1, date = 1L, distanceKm = 2.0, durationMinutes = 30),
            RunningRecord(id = 2, date = 2L, distanceKm = 1.0, durationMinutes = 15)
        )

        val summary = calculator.calculate(records)

        assertEquals(3.0, summary.totalDistanceKm, 0.0)
        assertEquals(180, summary.totalCalories)
        assertEquals(45, summary.totalDurationMinutes)
        assertTrue(summary.averagePace.isAvailable)
        assertEquals(15, summary.averagePace.minutes)
        assertEquals(0, summary.averagePace.seconds)
    }
}
