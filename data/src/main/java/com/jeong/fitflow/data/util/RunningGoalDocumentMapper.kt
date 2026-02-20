package com.jeong.fitflow.data.util

import com.google.firebase.firestore.DocumentSnapshot
import com.jeong.fitflow.data.contract.RunningGoalFirestoreFields
import com.jeong.fitflow.data.local.RunningGoalEntity

fun DocumentSnapshot.toRunningGoalEntity(): RunningGoalEntity? =
    if (!exists()) {
        null
    } else {
        getDouble(RunningGoalFirestoreFields.WEEKLY_GOAL_KM)
            ?.let { RunningGoalEntity(weeklyGoalKm = it) }
    }
