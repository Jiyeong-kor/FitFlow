package com.jeong.runninggoaltracker.feature.record.recognition

import com.jeong.runninggoaltracker.feature.record.api.model.ActivityLogEntry
import com.jeong.runninggoaltracker.feature.record.api.model.ActivityRecognitionStatus
import com.jeong.runninggoaltracker.feature.record.contract.ActivityRecognitionContract
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Singleton
class ActivityLogBuffer @Inject constructor() {

    private val _logs = MutableStateFlow<List<ActivityLogEntry>>(emptyList())
    val logs: StateFlow<List<ActivityLogEntry>> = _logs

    fun add(status: ActivityRecognitionStatus, timestamp: Long) {
        val newEntry = ActivityLogEntry(time = timestamp, status = status)
        _logs.update { current ->
            if (current.firstOrNull()?.status == status) {
                current
            } else {
                (listOf(newEntry) + current)
                    .take(ActivityRecognitionContract.ACTIVITY_LOG_MAX_SIZE)
            }
        }
    }
}
