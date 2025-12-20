package com.jeong.runninggoaltracker.feature.record.recognition

interface ActivityRecognitionController {
    fun startUpdates(onPermissionRequired: () -> Unit)
    fun stopUpdates()
    fun notifyPermissionDenied()
}
