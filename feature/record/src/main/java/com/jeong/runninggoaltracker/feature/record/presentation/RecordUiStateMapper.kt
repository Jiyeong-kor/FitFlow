package com.jeong.runninggoaltracker.feature.record.presentation

import com.jeong.runninggoaltracker.domain.model.ActivityCaloriesSegment
import com.jeong.runninggoaltracker.domain.usecase.EstimateActivityCaloriesUseCase
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.util.RunningMetricsCalculator
import com.jeong.runninggoaltracker.feature.record.api.model.ActivityLogEntry
import com.jeong.runninggoaltracker.feature.record.api.model.ActivityState
import com.jeong.runninggoaltracker.feature.record.api.model.RunningTrackerState
import com.jeong.runninggoaltracker.feature.record.contract.RecordCaloriesContract
import javax.inject.Inject
import kotlin.math.roundToInt

class RecordUiStateMapper @Inject constructor(
    private val metricsCalculator: RunningMetricsCalculator,
    private val estimateActivityCaloriesUseCase: EstimateActivityCaloriesUseCase
) {
    fun map(
        records: List<RunningRecord>,
        activity: ActivityState,
        tracker: RunningTrackerState,
        logs: List<ActivityLogEntry>
    ): RecordUiState {
        val displayDistanceKm = tracker.distanceKm
        val elapsedTime = metricsCalculator.calculateElapsedTime(tracker.elapsedMillis)
        val pace = metricsCalculator.calculatePace(displayDistanceKm, tracker.elapsedMillis)
        val displayStatus = activity.status
        val calories = estimateCalories(logs, tracker)
        return RecordUiState(
            records = records,
            activityStatus = displayStatus,
            isTracking = tracker.isTracking,
            distanceKm = displayDistanceKm,
            elapsedMillis = tracker.elapsedMillis,
            elapsedTime = elapsedTime.toUiState(),
            pace = pace.toUiState(),
            calories = calories,
            isPermissionRequired = tracker.isPermissionRequired
        )
    }

    private fun estimateCalories(logs: List<ActivityLogEntry>, tracker: RunningTrackerState): Int {
        if (tracker.startedAtEpochMillis <= 0L || tracker.updatedAtEpochMillis <= tracker.startedAtEpochMillis) {
            return 0
        }
        val segments = buildSegments(logs, tracker)
        val kcal = estimateActivityCaloriesUseCase(
            segments = segments,
            userWeightKg = RecordCaloriesContract.DEFAULT_USER_WEIGHT_KG
        )
        return kcal.roundToInt()
    }

    private fun buildSegments(
        logs: List<ActivityLogEntry>,
        tracker: RunningTrackerState
    ): List<ActivityCaloriesSegment> {
        val boundedLogs = logs
            .asReversed()
            .filter { it.time in tracker.startedAtEpochMillis..tracker.updatedAtEpochMillis }

        val changePoints = mutableListOf<ActivityLogEntry>()
        var previousStatus = ActivityLogEntry(
            time = tracker.startedAtEpochMillis,
            status = boundedLogs.firstOrNull()?.status ?: trackerStatusFallback(logs)
        ).status
        changePoints += ActivityLogEntry(time = tracker.startedAtEpochMillis, status = previousStatus)

        boundedLogs.forEach { entry ->
            if (entry.status != previousStatus && entry.time >= tracker.startedAtEpochMillis) {
                changePoints += entry
                previousStatus = entry.status
            }
        }

        val tailStatus = boundedLogs.lastOrNull()?.status ?: previousStatus
        if (changePoints.last().time < tracker.updatedAtEpochMillis) {
            changePoints += ActivityLogEntry(
                time = tracker.updatedAtEpochMillis,
                status = tailStatus
            )
        }

        if (changePoints.size < 2) {
            return emptyList()
        }

        return changePoints.zipWithNext { start, end ->
            ActivityCaloriesSegment(
                activityType = start.status.toCardioActivityType(),
                durationSeconds = ((end.time - start.time) / 1000L).coerceAtLeast(0L)
            )
        }
    }

    private fun trackerStatusFallback(logs: List<ActivityLogEntry>) =
        logs.lastOrNull()?.status ?: com.jeong.runninggoaltracker.feature.record.api.model.ActivityRecognitionStatus.Unknown
}
