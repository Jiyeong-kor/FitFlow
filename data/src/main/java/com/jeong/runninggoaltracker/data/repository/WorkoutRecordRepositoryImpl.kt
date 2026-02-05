package com.jeong.runninggoaltracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.runninggoaltracker.data.contract.FirestorePaths
import com.jeong.runninggoaltracker.data.local.WorkoutRecordDao
import com.jeong.runninggoaltracker.data.util.awaitResult
import com.jeong.runninggoaltracker.data.local.toEntity
import com.jeong.runninggoaltracker.domain.model.WorkoutRecord
import com.jeong.runninggoaltracker.domain.repository.WorkoutRecordRepository
import javax.inject.Inject

class WorkoutRecordRepositoryImpl @Inject constructor(
    private val workoutRecordDao: WorkoutRecordDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : WorkoutRecordRepository {
    override suspend fun upsertRecord(record: WorkoutRecord) {
        workoutRecordDao.upsertRecord(record.toEntity())
        uploadRecordIfNeeded(record)
    }

    private suspend fun uploadRecordIfNeeded(record: WorkoutRecord) {
        val user = firebaseAuth.currentUser ?: return
        if (user.isAnonymous) return
        val docId = "${record.date}-${record.exerciseType.name}"
        val docRef = firestore.collection(FirestorePaths.COLLECTION_USERS)
            .document(user.uid)
            .collection(FirestorePaths.COLLECTION_WORKOUT_RECORDS)
            .document(docId)
        val data = mapOf(
            "date" to record.date,
            "exerciseType" to record.exerciseType.name,
            "repCount" to record.repCount
        )
        try {
            docRef.set(data).awaitResult()
        } catch (_: Exception) {
        }
    }
}
