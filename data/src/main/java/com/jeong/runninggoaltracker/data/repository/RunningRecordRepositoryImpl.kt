package com.jeong.runninggoaltracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.runninggoaltracker.data.contract.FirestorePaths
import com.jeong.runninggoaltracker.data.contract.RunningRecordFirestoreFields
import com.jeong.runninggoaltracker.data.local.RunningRecordDao
import com.jeong.runninggoaltracker.data.util.awaitResult
import com.jeong.runninggoaltracker.data.local.toDomain
import com.jeong.runninggoaltracker.data.local.toEntity
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.repository.RunningRecordRepository
import com.jeong.runninggoaltracker.domain.di.IoDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RunningRecordRepositoryImpl @Inject constructor(
    private val recordDao: RunningRecordDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
) : RunningRecordRepository {

    override fun getAllRecords(): Flow<List<RunningRecord>> =
        recordDao.getAllRecords().map { records ->
            records.map { it.toDomain() }
        }.distinctUntilChanged()
            .flowOn(ioDispatcher)

    override suspend fun addRecord(record: RunningRecord): Long {
        val id = withContext(ioDispatcher) {
            recordDao.insertRecord(record.toEntity())
        }
        val recordWithId = if (record.id == 0L) record.copy(id = id) else record
        uploadRecordIfNeeded(recordWithId)
        return id
    }

    private suspend fun uploadRecordIfNeeded(record: RunningRecord) {
        val user = firebaseAuth.currentUser ?: return
        if (user.isAnonymous) return
        val docRef = firestore.collection(FirestorePaths.COLLECTION_USERS)
            .document(user.uid)
            .collection(FirestorePaths.COLLECTION_RUNNING_RECORDS)
            .document(record.id.toString())
        val data = mapOf(
            RunningRecordFirestoreFields.DATE to record.date,
            RunningRecordFirestoreFields.DISTANCE_KM to record.distanceKm,
            RunningRecordFirestoreFields.DURATION_MINUTES to record.durationMinutes
        )
        try {
            docRef.set(data).awaitResult()
        } catch (_: Exception) {
        }
    }
}
