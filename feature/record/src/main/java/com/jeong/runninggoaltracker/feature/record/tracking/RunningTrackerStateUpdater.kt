package com.jeong.runninggoaltracker.feature.record.tracking

interface RunningTrackerStateUpdater {
    fun markTracking(startedAtEpochMillis: Long)
    fun update(distanceKm: Double, elapsedMillis: Long, updatedAtEpochMillis: Long)
    fun stop()
    fun markPermissionRequired()
}
