package com.jeong.runninggoaltracker.feature.record.recognition

import android.content.Context
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.StateFlow

interface ActivityRecognitionMonitor {
    val activityState: StateFlow<ActivityState>
    val activityLogs: StateFlow<List<ActivityLogEntry>>
}

object ActivityRecognitionMonitorHolder : ActivityRecognitionMonitor {
    private var stateHolder: ActivityRecognitionStateHolder? = null

    fun initialize(context: Context) {
        if (stateHolder != null) return
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ActivityRecognitionMonitorEntryPoint::class.java
        )
        stateHolder = entryPoint.activityRecognitionStateHolder()
    }

    override val activityState: StateFlow<ActivityState>
        get() = requireNotNull(stateHolder).state

    override val activityLogs: StateFlow<List<ActivityLogEntry>>
        get() = ActivityLogHolder.logs

    @dagger.hilt.EntryPoint
    @dagger.hilt.InstallIn(SingletonComponent::class)
    interface ActivityRecognitionMonitorEntryPoint {
        fun activityRecognitionStateHolder(): ActivityRecognitionStateHolder
    }
}
