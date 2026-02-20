package com.jeong.fitflow.feature.record.tracking

interface RunningTrackerStateUpdater {
    fun markTracking(startedAtEpochMillis: Long)
    fun update(distanceKm: Double, elapsedMillis: Long, updatedAtEpochMillis: Long)
    fun stop()
    fun markPermissionRequired()
}
