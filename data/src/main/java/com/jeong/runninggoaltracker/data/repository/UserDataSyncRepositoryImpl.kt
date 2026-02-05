package com.jeong.runninggoaltracker.data.repository

import androidx.room.withTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.runninggoaltracker.data.contract.FirestorePaths
import com.jeong.runninggoaltracker.data.local.RunningDatabase
import com.jeong.runninggoaltracker.data.local.RunningGoalEntity
import com.jeong.runninggoaltracker.data.local.RunningRecordEntity
import com.jeong.runninggoaltracker.data.local.RunningReminderEntity
import com.jeong.runninggoaltracker.data.local.WorkoutRecordEntity
import com.jeong.runninggoaltracker.data.util.awaitResult
import com.jeong.runninggoaltracker.data.util.toAuthError
import com.jeong.runninggoaltracker.domain.model.AuthError
import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.repository.UserDataSyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDataSyncRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val runningDatabase: RunningDatabase
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
                val id = doc.id.toLongOrNull() ?: return@mapNotNull null
                val date = doc.getLong("date") ?: return@mapNotNull null
                val distanceKm = doc.getDouble("distanceKm") ?: return@mapNotNull null
                val durationMinutes =
                    doc.getLong("durationMinutes")?.toInt() ?: return@mapNotNull null
                RunningRecordEntity(
                    id = id,
                    date = date,
                    distanceKm = distanceKm,
                    durationMinutes = durationMinutes
                )
            }

            val reminderEntities = remindersSnapshot.documents.mapNotNull { doc ->
                val id = doc.id.toIntOrNull() ?: return@mapNotNull null
                val hour = doc.getLong("hour")?.toInt() ?: return@mapNotNull null
                val minute = doc.getLong("minute")?.toInt() ?: return@mapNotNull null
                val isEnabled = doc.getBoolean("enabled") ?: return@mapNotNull null
                val days = doc.get("days") as? List<*> ?: emptyList<Any>()
                val daySet = days.mapNotNull { (it as? Number)?.toInt() }.toSet()
                RunningReminderEntity(
                    id = id,
                    hour = hour,
                    minute = minute,
                    isEnabled = isEnabled,
                    days = daySet
                )
            }

            val workoutEntities = workoutSnapshot.documents.mapNotNull { doc ->
                val date = doc.getLong("date") ?: return@mapNotNull null
                val exerciseType = doc.getString("exerciseType") ?: return@mapNotNull null
                val repCount = doc.getLong("repCount")?.toInt() ?: return@mapNotNull null
                WorkoutRecordEntity(
                    date = date,
                    exerciseType = exerciseType,
                    repCount = repCount
                )
            }

            val goalEntity = if (goalSnapshot.exists()) {
                val weeklyGoalKm = goalSnapshot.getDouble("weeklyGoalKm")
                weeklyGoalKm?.let { RunningGoalEntity(weeklyGoalKm = it) }
            } else {
                null
            }

            withContext(Dispatchers.IO) {
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
