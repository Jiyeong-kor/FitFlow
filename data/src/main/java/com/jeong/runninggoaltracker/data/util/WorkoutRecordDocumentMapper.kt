package com.jeong.runninggoaltracker.data.util

import com.google.firebase.firestore.DocumentSnapshot
import com.jeong.runninggoaltracker.data.contract.WorkoutRecordFirestoreFields
import com.jeong.runninggoaltracker.data.local.WorkoutRecordEntity

fun DocumentSnapshot.toWorkoutRecordEntity(): WorkoutRecordEntity? {
    val date = getLong(WorkoutRecordFirestoreFields.DATE) ?: return null
    val exerciseType = getString(WorkoutRecordFirestoreFields.EXERCISE_TYPE) ?: return null
    val repCount = getLong(WorkoutRecordFirestoreFields.REP_COUNT)?.toInt() ?: return null
    return WorkoutRecordEntity(
        date = date,
        exerciseType = exerciseType,
        repCount = repCount
    )
}
