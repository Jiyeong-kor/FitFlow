package com.jeong.fitflow.feature.record.presentation

import com.jeong.fitflow.domain.usecase.EstimateActivityCaloriesUseCase
import com.jeong.fitflow.domain.util.RunningMetricsCalculator
import com.jeong.fitflow.feature.record.api.model.ActivityLogEntry
import com.jeong.fitflow.feature.record.api.model.ActivityRecognitionStatus
import com.jeong.fitflow.feature.record.api.model.ActivityState
import com.jeong.fitflow.feature.record.api.model.RunningTrackerState
import org.junit.Assert.assertEquals
import kotlin.math.roundToInt
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordUiStateMapperTest {

    private val mapper = RecordUiStateMapper(
        metricsCalculator = RunningMetricsCalculator(),
        estimateActivityCaloriesUseCase = EstimateActivityCaloriesUseCase()
    )

    @Test
    fun `ui state uses tracker distance and detected activity status`() {
        val uiState = mapper.map(
            records = emptyList(),
            activity = ActivityState(status = ActivityRecognitionStatus.Running),
            tracker = RunningTrackerState(
                isTracking = true,
                distanceKm = 1.2,
                elapsedMillis = 600_000L,
                startedAtEpochMillis = 1_000L,
                updatedAtEpochMillis = 601_000L,
                isPermissionRequired = false
            ),
            logs = listOf(ActivityLogEntry(1_000L, ActivityRecognitionStatus.Running))
        )

        assertEquals(ActivityRecognitionStatus.Running, uiState.activityStatus)
        assertEquals(1.2, uiState.distanceKm, 0.0)
        assertTrue(uiState.calories > 0)
        assertTrue(uiState.pace.isAvailable)
    }

    @Test
    fun `non running walking bicycle statuses do not contribute calories`() {
        val uiState = mapper.map(
            records = emptyList(),
            activity = ActivityState(status = ActivityRecognitionStatus.Still),
            tracker = RunningTrackerState(
                isTracking = true,
                elapsedMillis = 300_000L,
                startedAtEpochMillis = 1_000L,
                updatedAtEpochMillis = 301_000L
            ),
            logs = listOf(ActivityLogEntry(1_000L, ActivityRecognitionStatus.Still))
        )

        assertEquals(0, uiState.calories)
    }

    @Test
    fun `segment calories are accumulated by activity changes`() {
        val uiState = mapper.map(
            records = emptyList(),
            activity = ActivityState(status = ActivityRecognitionStatus.Walking),
            tracker = RunningTrackerState(
                isTracking = true,
                elapsedMillis = 900_000L,
                startedAtEpochMillis = 1_000L,
                updatedAtEpochMillis = 901_000L
            ),
            logs = listOf(
                ActivityLogEntry(601_000L, ActivityRecognitionStatus.Walking),
                ActivityLogEntry(1_000L, ActivityRecognitionStatus.Running)
            )
        )

        val expectedRunning = 10.0 * (9.3 * 3.5 * 70.0) / 200.0
        val expectedWalking = 5.0 * (3.5 * 3.5 * 70.0) / 200.0
        assertEquals((expectedRunning + expectedWalking).roundToInt(), uiState.calories)
    }
}
