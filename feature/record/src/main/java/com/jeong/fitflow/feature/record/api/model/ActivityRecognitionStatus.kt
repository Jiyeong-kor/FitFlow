package com.jeong.fitflow.feature.record.api.model

enum class ActivityRecognitionStatus {
    Unknown,
    NoPermission,
    RequestFailed,
    SecurityException,
    NoResult,
    NoActivity,
    Stopped,
    Running,
    Walking,
    OnBicycle,
    InVehicle,
    Still
}
