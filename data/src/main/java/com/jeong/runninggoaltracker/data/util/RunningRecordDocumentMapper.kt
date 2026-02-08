package com.jeong.runninggoaltracker.data.util

import com.google.firebase.firestore.DocumentSnapshot
import com.jeong.runninggoaltracker.data.contract.RunningRecordFirestoreFields
import com.jeong.runninggoaltracker.data.local.RunningRecordEntity

fun DocumentSnapshot.toRunningRecordEntity(): RunningRecordEntity? {
    val id = id.toLongOrNull() ?: return null
    val date = getLong(RunningRecordFirestoreFields.DATE) ?: return null
    val distanceKm = getDouble(RunningRecordFirestoreFields.DISTANCE_KM) ?: return null
    val durationMinutes = getLong(RunningRecordFirestoreFields.DURATION_MINUTES)?.toInt() ?: return null
    return RunningRecordEntity(
        id = id,
        date = date,
        distanceKm = distanceKm,
        durationMinutes = durationMinutes
    )
}
