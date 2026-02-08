package com.jeong.runninggoaltracker.data.util

import com.google.firebase.firestore.DocumentSnapshot
import com.jeong.runninggoaltracker.data.contract.RunningGoalFirestoreFields
import com.jeong.runninggoaltracker.data.local.RunningGoalEntity

fun DocumentSnapshot.toRunningGoalEntity(): RunningGoalEntity? =
    if (!exists()) {
        null
    } else {
        getDouble(RunningGoalFirestoreFields.WEEKLY_GOAL_KM)
            ?.let { RunningGoalEntity(weeklyGoalKm = it) }
    }
