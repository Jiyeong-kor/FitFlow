package com.jeong.fitflow.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.fitflow.data.contract.FirestorePaths
import com.jeong.fitflow.data.contract.WorkoutRecordFirestoreFields
import com.jeong.fitflow.data.local.SyncOutboxDao
import com.jeong.fitflow.data.local.SyncOutboxEntity
import com.jeong.fitflow.data.local.SyncOutboxType
import com.jeong.fitflow.data.local.WorkoutRecordDao
import com.jeong.fitflow.data.local.toDomain
import com.jeong.fitflow.data.local.toEntity
import com.jeong.fitflow.data.util.awaitResult
import com.jeong.fitflow.domain.di.IoDispatcher
import com.jeong.fitflow.domain.model.WorkoutRecord
import com.jeong.fitflow.domain.repository.WorkoutRecordRepository
import com.jeong.fitflow.shared.logging.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkoutRecordRepositoryImpl @Inject constructor(
    private val workoutRecordDao: WorkoutRecordDao,
    private val syncOutboxDao: SyncOutboxDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val appLogger: AppLogger
) : WorkoutRecordRepository {
    override fun getAllRecords(): Flow<List<WorkoutRecord>> =
        workoutRecordDao.getAllRecords().map { entities ->
            entities.map { entity -> entity.toDomain() }
        }.distinctUntilChanged()
            .flowOn(ioDispatcher)

    override suspend fun upsertRecord(record: WorkoutRecord) {
        withContext(ioDispatcher) {
            workoutRecordDao.upsertRecord(record.toEntity())
        }
        uploadRecordIfNeeded(record)
    }

    private suspend fun uploadRecordIfNeeded(record: WorkoutRecord) {
        val user = firebaseAuth.currentUser ?: return
        if (user.isAnonymous) return

        flushPendingUploads(user.uid)

        val docId = "${record.date}-${record.exerciseType.name}"
        val data = mapOf(
            WorkoutRecordFirestoreFields.DATE to record.date,
            WorkoutRecordFirestoreFields.EXERCISE_TYPE to record.exerciseType.name,
            WorkoutRecordFirestoreFields.REP_COUNT to record.repCount
        )
        try {
            firestore.collection(FirestorePaths.COLLECTION_USERS)
                .document(user.uid)
                .collection(FirestorePaths.COLLECTION_WORKOUT_RECORDS)
                .document(docId)
                .set(data)
                .awaitResult()
            syncOutboxDao.delete(SyncOutboxType.WORKOUT_RECORD, docId)
        } catch (exception: Exception) {
            handleRemoteSyncFailure(exception)
            syncOutboxDao.upsert(
                SyncOutboxEntity(
                    syncType = SyncOutboxType.WORKOUT_RECORD,
                    docId = docId,
                    date = record.date,
                    exerciseType = record.exerciseType.name,
                    repCount = record.repCount
                )
            )
        }
    }

    private suspend fun flushPendingUploads(uid: String) {
        val pending = syncOutboxDao.getPending(50)
        for (entry in pending) {
            if (entry.syncType != SyncOutboxType.WORKOUT_RECORD) continue
            val exerciseType = entry.exerciseType ?: continue
            val payload = mapOf(
                WorkoutRecordFirestoreFields.DATE to entry.date,
                WorkoutRecordFirestoreFields.EXERCISE_TYPE to exerciseType,
                WorkoutRecordFirestoreFields.REP_COUNT to (entry.repCount ?: 0)
            )
            try {
                firestore.collection(FirestorePaths.COLLECTION_USERS)
                    .document(uid)
                    .collection(FirestorePaths.COLLECTION_WORKOUT_RECORDS)
                    .document(entry.docId)
                    .set(payload)
                    .awaitResult()
                syncOutboxDao.delete(entry.syncType, entry.docId)
            } catch (exception: Exception) {
                handleRemoteSyncFailure(exception)
                syncOutboxDao.incrementRetry(entry.syncType, entry.docId)
            }
        }
    }

    private fun handleRemoteSyncFailure(exception: Exception) {
        appLogger.warning(
            tag = LOG_TAG,
            message = WORKOUT_SYNC_FAILURE_LOG,
            throwable = exception
        )
    }

    private companion object {
        const val LOG_TAG = "WorkoutRecordRepository"
        const val WORKOUT_SYNC_FAILURE_LOG = "Workout record remote sync failed"
    }
}
