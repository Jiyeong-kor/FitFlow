package com.jeong.runninggoaltracker.feature.record.presentation

import com.jeong.runninggoaltracker.domain.usecase.GetRunningRecordsUseCase
import com.jeong.runninggoaltracker.feature.record.api.ActivityRecognitionMonitor
import com.jeong.runninggoaltracker.feature.record.api.RunningTrackerMonitor
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
        runningTrackerMonitor.trackerState
    ) { records, activity, tracker ->
        uiStateMapper.map(records, activity, tracker)
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = RecordUiState()
    )
}
