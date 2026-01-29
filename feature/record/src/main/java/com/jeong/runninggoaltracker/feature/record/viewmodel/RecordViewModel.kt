package com.jeong.runninggoaltracker.feature.record.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRecordsUseCase
import com.jeong.runninggoaltracker.domain.util.RunningMetricsCalculator
import com.jeong.runninggoaltracker.feature.record.presentation.RecordUiState
import com.jeong.runninggoaltracker.feature.record.presentation.toUiState
import com.jeong.runninggoaltracker.feature.record.api.ActivityRecognitionController
import com.jeong.runninggoaltracker.feature.record.api.ActivityRecognitionMonitor
import com.jeong.runninggoaltracker.feature.record.api.RunningTrackerController
import com.jeong.runninggoaltracker.feature.record.api.RunningTrackerMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    getRunningRecordsUseCase: GetRunningRecordsUseCase,
    private val activityRecognitionController: ActivityRecognitionController,
    activityRecognitionMonitor: ActivityRecognitionMonitor,
    private val runningTrackerController: RunningTrackerController,
    runningTrackerMonitor: RunningTrackerMonitor,
    private val metricsCalculator: RunningMetricsCalculator
) : ViewModel() {

    val uiState: StateFlow<RecordUiState> = combine(
        getRunningRecordsUseCase(),
        activityRecognitionMonitor.activityState,
        runningTrackerMonitor.trackerState
    ) { records, activity, tracker ->
        val elapsedTime = metricsCalculator.calculateElapsedTime(tracker.elapsedMillis)
        val pace = metricsCalculator.calculatePace(tracker.distanceKm, tracker.elapsedMillis)
        RecordUiState(
            records = records,
            activityStatus = activity.status,
            isTracking = tracker.isTracking,
            distanceKm = tracker.distanceKm,
            elapsedMillis = tracker.elapsedMillis,
            elapsedTime = elapsedTime.toUiState(),
            pace = pace.toUiState(),
            permissionRequired = tracker.permissionRequired
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = RecordUiState()
    )

    fun startActivityRecognition() = activityRecognitionController.startUpdates()

    fun stopActivityRecognition() = activityRecognitionController.stopUpdates()

    fun startTracking() = runningTrackerController.startTracking()

    fun stopTracking() = runningTrackerController.stopTracking()
}
