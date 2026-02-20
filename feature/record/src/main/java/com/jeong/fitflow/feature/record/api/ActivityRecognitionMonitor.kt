package com.jeong.fitflow.feature.record.api

import com.jeong.fitflow.feature.record.api.model.ActivityLogEntry
import com.jeong.fitflow.feature.record.api.model.ActivityState
import kotlinx.coroutines.flow.StateFlow

interface ActivityRecognitionMonitor {
    val activityState: StateFlow<ActivityState>
    val activityLogs: StateFlow<List<ActivityLogEntry>>
}
