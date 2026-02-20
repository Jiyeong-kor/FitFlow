package com.jeong.fitflow.domain.util

import com.jeong.fitflow.domain.model.RunningPace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RunningMetricsCalculatorTest {

    private val calculator = RunningMetricsCalculator()

    @Test
    fun `calculatePace returns unavailable when distance is zero`() {
        val pace = calculator.calculatePace(distanceKm = 0.0, elapsedMillis = 1_000L)

        assertUnavailablePace(pace)
    }

    @Test
    fun `calculatePace returns unavailable when elapsed time is zero`() {
        val pace = calculator.calculatePace(distanceKm = 1.0, elapsedMillis = 0L)

        assertUnavailablePace(pace)
    }

    @Test
    fun `calculatePace returns minutes and seconds when available`() {
        val pace = calculator.calculatePace(distanceKm = 1.0, elapsedMillis = 245_000L)

        assertTrue(pace.isAvailable)
        assertEquals(4, pace.minutes)
        assertEquals(5, pace.seconds)
    }

    private fun assertUnavailablePace(pace: RunningPace) {
        assertFalse(pace.isAvailable)
        assertEquals(0, pace.minutes)
        assertEquals(0, pace.seconds)
    }
}
