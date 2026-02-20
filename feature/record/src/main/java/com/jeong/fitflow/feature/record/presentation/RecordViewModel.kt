package com.jeong.fitflow.feature.record.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.fitflow.domain.usecase.GetRunningRecordsUseCase
import com.jeong.fitflow.feature.record.api.ActivityRecognitionController
import com.jeong.fitflow.feature.record.api.ActivityRecognitionMonitor
import com.jeong.fitflow.feature.record.api.RunningTrackerController
import com.jeong.fitflow.feature.record.api.RunningTrackerMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    getRunningRecordsUseCase: GetRunningRecordsUseCase,
    private val activityRecognitionController: ActivityRecognitionController,
    activityRecognitionMonitor: ActivityRecognitionMonitor,
    private val runningTrackerController: RunningTrackerController,
    runningTrackerMonitor: RunningTrackerMonitor,
    uiStateMapper: RecordUiStateMapper
) : ViewModel() {

    private val stateHolder = RecordStateHolder(
        scope = viewModelScope,
        getRunningRecordsUseCase = getRunningRecordsUseCase,
        activityRecognitionMonitor = activityRecognitionMonitor,
        runningTrackerMonitor = runningTrackerMonitor,
        uiStateMapper = uiStateMapper
    )

    val uiState: StateFlow<RecordUiState> = stateHolder.uiState

    fun startActivityRecognition() {
        activityRecognitionController.startUpdates()
    }

    fun stopActivityRecognition() {
        activityRecognitionController.stopUpdates()
    }

    fun startTracking() {
        runningTrackerController.startTracking()
    }

    fun stopTracking() {
        runningTrackerController.stopTracking()
    }
}
