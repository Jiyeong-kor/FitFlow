package com.jeong.runninggoaltracker.feature.record.recognition

import kotlinx.coroutines.flow.StateFlow

interface ActivityRecognitionMonitor {
    val activityState: StateFlow<ActivityState>
    val activityLogs: StateFlow<List<ActivityLogEntry>>
}

object ActivityRecognitionMonitorHolder : ActivityRecognitionMonitor {
    override val activityState: StateFlow<ActivityState>
        get() = ActivityRecognitionStateHolder.state

    override val activityLogs: StateFlow<List<ActivityLogEntry>>
        get() = ActivityLogHolder.logs
}
