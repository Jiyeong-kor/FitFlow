package com.jeong.runninggoaltracker.feature.record.recognition

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ActivityState(
    val label: String = "UNKNOWN"
)

@Singleton
class ActivityRecognitionStateHolder @Inject constructor() {

    private val _state = MutableStateFlow(ActivityState())
    val state: StateFlow<ActivityState> = _state

    fun update(label: String) {
        _state.value = ActivityState(
            label = label,
        )
    }
}
