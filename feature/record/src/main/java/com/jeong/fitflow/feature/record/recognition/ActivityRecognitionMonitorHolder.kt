package com.jeong.fitflow.feature.record.recognition

import com.jeong.fitflow.feature.record.api.ActivityRecognitionMonitor
import com.jeong.fitflow.feature.record.api.model.ActivityLogEntry
import com.jeong.fitflow.feature.record.api.model.ActivityState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.StateFlow

@Singleton
class ActivityRecognitionMonitorHolder @Inject constructor(
    private val stateHolder: ActivityRecognitionStateHolder,
    private val logBuffer: ActivityLogBuffer
) : ActivityRecognitionMonitor {
    override val activityState: StateFlow<ActivityState>
        get() = stateHolder.state

    override val activityLogs: StateFlow<List<ActivityLogEntry>>
        get() = logBuffer.logs
}
