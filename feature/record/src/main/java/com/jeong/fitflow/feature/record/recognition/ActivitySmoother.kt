package com.jeong.fitflow.feature.record.recognition

import com.jeong.fitflow.feature.record.api.model.ActivityRecognitionStatus
import com.jeong.fitflow.feature.record.contract.ActivityRecognitionContract
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class ActivitySmoother @Inject constructor() {

    private val buffer = MutableStateFlow<List<ActivityRecognitionStatus>>(emptyList())

    fun push(status: ActivityRecognitionStatus): ActivityRecognitionStatus {
        var smoothed = status
        buffer.update { current ->
            val updated =
                (current + status).takeLast(ActivityRecognitionContract.ACTIVITY_SMOOTHING_WINDOW_SIZE)
            val counted = updated.groupingBy { it }.eachCount()
            smoothed = counted.maxByOrNull { it.value }?.key ?: status
            updated
        }
        return smoothed
    }
}
