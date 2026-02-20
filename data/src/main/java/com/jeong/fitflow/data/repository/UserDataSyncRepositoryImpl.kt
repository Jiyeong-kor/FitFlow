package com.jeong.fitflow.data.repository

import androidx.room.withTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.fitflow.data.contract.FirestorePaths
import com.jeong.fitflow.data.local.RunningDatabase
import com.jeong.fitflow.data.util.awaitResult
import com.jeong.fitflow.data.util.toAuthError
import com.jeong.fitflow.data.util.toRunningGoalEntity
import com.jeong.fitflow.data.util.toRunningRecordEntity
import com.jeong.fitflow.data.util.toRunningReminderEntity
import com.jeong.fitflow.data.util.toWorkoutRecordEntity
import com.jeong.fitflow.domain.model.AuthError
import com.jeong.fitflow.domain.model.AuthResult
import com.jeong.fitflow.domain.repository.UserDataSyncRepository
import com.jeong.fitflow.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDataSyncRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val runningDatabase: RunningDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserDataSyncRepository {

    override suspend fun restoreUserDataFromRemote(): AuthResult<Unit> {
        val user = firebaseAuth.currentUser ?: return AuthResult.Failure(AuthError.PermissionDenied)
        if (user.isAnonymous) {
            return AuthResult.Failure(AuthError.PermissionDenied)
        }
        return try {
            val uid = user.uid
            val userDoc = firestore.collection(FirestorePaths.COLLECTION_USERS).document(uid)
            val recordsSnapshot = userDoc
                .collection(FirestorePaths.COLLECTION_RUNNING_RECORDS)
                .get()
                .awaitResult()
            val remindersSnapshot = userDoc
                .collection(FirestorePaths.COLLECTION_RUNNING_REMINDERS)
                .get()
                .awaitResult()
            val workoutSnapshot = userDoc
                .collection(FirestorePaths.COLLECTION_WORKOUT_RECORDS)
                .get()
                .awaitResult()
            val goalSnapshot = userDoc
                .collection(FirestorePaths.COLLECTION_RUNNING_GOALS)
                .document(FirestorePaths.DOC_RUNNING_GOAL)
                .get()
                .awaitResult()

            val recordEntities = recordsSnapshot.documents.mapNotNull { doc ->
                doc.toRunningRecordEntity()
            }

            val reminderEntities = remindersSnapshot.documents.mapNotNull { doc ->
                doc.toRunningReminderEntity()
            }

            val workoutEntities = workoutSnapshot.documents.mapNotNull { doc ->
                doc.toWorkoutRecordEntity()
            }

            val goalEntity = goalSnapshot.toRunningGoalEntity()

            withContext(ioDispatcher) {
                runningDatabase.withTransaction {
                    runningDatabase.clearAllTables()
                    recordEntities.forEach { runningDatabase.runningRecordDao().insertRecord(it) }
                    reminderEntities.forEach {
                        runningDatabase.runningReminderDao().upsertReminder(it)
                    }
                    workoutEntities.forEach { runningDatabase.workoutRecordDao().upsertRecord(it) }
                    goalEntity?.let { runningDatabase.runningGoalDao().upsertGoal(it) }
                }
            }
            AuthResult.Success(Unit)
        } catch (error: Exception) {
            AuthResult.Failure(error.toAuthError())
        }
    }
}
