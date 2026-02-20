package com.jeong.fitflow.feature.record.presentation

import com.jeong.fitflow.domain.usecase.GetRunningRecordsUseCase
import com.jeong.fitflow.feature.record.api.ActivityRecognitionMonitor
import com.jeong.fitflow.feature.record.api.RunningTrackerMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RecordStateHolder(
    scope: CoroutineScope,
    getRunningRecordsUseCase: GetRunningRecordsUseCase,
    activityRecognitionMonitor: ActivityRecognitionMonitor,
    runningTrackerMonitor: RunningTrackerMonitor,
    private val uiStateMapper: RecordUiStateMapper
) {

    val uiState: StateFlow<RecordUiState> = combine(
        getRunningRecordsUseCase(),
        activityRecognitionMonitor.activityState,
        runningTrackerMonitor.trackerState,
        activityRecognitionMonitor.activityLogs
    ) { records, activity, tracker, logs ->
        uiStateMapper.map(records, activity, tracker, logs)
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = RecordUiState()
    )
}
