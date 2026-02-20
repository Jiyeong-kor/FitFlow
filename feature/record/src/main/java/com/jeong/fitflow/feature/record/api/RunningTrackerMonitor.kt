package com.jeong.fitflow.feature.record.api

import com.jeong.fitflow.feature.record.api.model.RunningTrackerState
import kotlinx.coroutines.flow.StateFlow

interface RunningTrackerMonitor {
    val trackerState: StateFlow<RunningTrackerState>
}
