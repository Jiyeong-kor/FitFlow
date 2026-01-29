package com.jeong.runninggoaltracker.feature.record.recognition

import com.jeong.runninggoaltracker.feature.record.api.model.ActivityRecognitionStatus
import com.jeong.runninggoaltracker.feature.record.contract.ActivityRecognitionContract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivitySmoother @Inject constructor() {

    private val buffer: ArrayDeque<ActivityRecognitionStatus> = ArrayDeque()

    fun push(status: ActivityRecognitionStatus): ActivityRecognitionStatus {
        buffer.addLast(status)
        if (buffer.size > ActivityRecognitionContract.ACTIVITY_SMOOTHING_WINDOW_SIZE) {
            buffer.removeFirst()
        }

        val counted = buffer.groupingBy { it }.eachCount()
        return counted.maxByOrNull { it.value }?.key ?: status
    }
}
