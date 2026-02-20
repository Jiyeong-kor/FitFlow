package com.jeong.fitflow.feature.record.tracking

import com.jeong.fitflow.feature.record.api.RunningTrackerMonitor
import com.jeong.fitflow.feature.record.api.model.RunningTrackerState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Singleton
class RunningTrackerStateHolder @Inject constructor() :
    RunningTrackerMonitor,
    RunningTrackerStateUpdater {

    private val _state = MutableStateFlow(RunningTrackerState())
    override val trackerState: StateFlow<RunningTrackerState> = _state

    override fun markTracking(startedAtEpochMillis: Long) {
        _state.update {
            it.copy(
                isTracking = true,
                isPermissionRequired = false,
                distanceKm = 0.0,
                elapsedMillis = 0L,
                startedAtEpochMillis = startedAtEpochMillis,
                updatedAtEpochMillis = startedAtEpochMillis
            )
        }
    }

    override fun update(distanceKm: Double, elapsedMillis: Long, updatedAtEpochMillis: Long) {
        _state.update {
            it.copy(
                distanceKm = distanceKm,
                elapsedMillis = elapsedMillis,
                updatedAtEpochMillis = updatedAtEpochMillis
            )
        }
    }

    override fun stop() {
        _state.update {
            it.copy(
                isTracking = false
            )
        }
    }

    override fun markPermissionRequired() {
        _state.update {
            it.copy(
                isPermissionRequired = true,
                isTracking = false
            )
        }
    }
}
