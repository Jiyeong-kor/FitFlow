package com.jeong.runninggoaltracker.feature.record.presentation

import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.util.RunningMetricsCalculator
import com.jeong.runninggoaltracker.feature.record.api.model.ActivityState
import com.jeong.runninggoaltracker.feature.record.api.model.RunningTrackerState
import javax.inject.Inject

class RecordUiStateMapper @Inject constructor(
    private val metricsCalculator: RunningMetricsCalculator
) {
    fun map(
        records: List<RunningRecord>,
        activity: ActivityState,
        tracker: RunningTrackerState
    ): RecordUiState {
        val elapsedTime = metricsCalculator.calculateElapsedTime(tracker.elapsedMillis)
        val pace = metricsCalculator.calculatePace(tracker.distanceKm, tracker.elapsedMillis)
        return RecordUiState(
            records = records,
            activityStatus = activity.status,
            isTracking = tracker.isTracking,
            distanceKm = tracker.distanceKm,
            elapsedMillis = tracker.elapsedMillis,
            elapsedTime = elapsedTime.toUiState(),
            pace = pace.toUiState(),
            isPermissionRequired = tracker.isPermissionRequired
        )
    }
}
