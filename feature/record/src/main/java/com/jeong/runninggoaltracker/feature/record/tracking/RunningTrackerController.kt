package com.jeong.runninggoaltracker.feature.record.tracking

interface RunningTrackerController {
    fun startTracking(onPermissionRequired: () -> Unit)
    fun stopTracking()
    fun notifyPermissionDenied()
}
