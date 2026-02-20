package com.jeong.fitflow.feature.record.api.model

data class ActivityLogEntry(
    val time: Long,
    val status: ActivityRecognitionStatus
)
