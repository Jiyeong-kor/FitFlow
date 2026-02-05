package com.jeong.runninggoaltracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.runninggoaltracker.data.contract.FirestorePaths
import com.jeong.runninggoaltracker.data.local.RunningReminderDao
import com.jeong.runninggoaltracker.data.util.awaitResult
import com.jeong.runninggoaltracker.data.local.toDomain
import com.jeong.runninggoaltracker.data.local.toEntity
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.repository.RunningReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RunningReminderRepositoryImpl @Inject constructor(
    private val reminderDao: RunningReminderDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : RunningReminderRepository {

    override fun getAllReminders(): Flow<List<RunningReminder>> =
        reminderDao.getAllReminders().map { reminders ->
            reminders.map { it.toDomain() }
        }

    override suspend fun upsertReminder(reminder: RunningReminder) {
        val reminderWithId = ensureReminderId(reminder)
        reminderDao.upsertReminder(reminderWithId.toEntity())
        uploadReminderIfNeeded(reminderWithId)
    }

    override suspend fun deleteReminder(reminderId: Int) {
        reminderDao.deleteReminder(reminderId)
        deleteReminderRemoteIfNeeded(reminderId)
    }

    private fun ensureReminderId(reminder: RunningReminder): RunningReminder {
        val existingId = reminder.id
        if (existingId != null) {
            return reminder
        }
        val generatedId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        return reminder.copy(id = generatedId)
    }

    private suspend fun uploadReminderIfNeeded(reminder: RunningReminder) {
        val user = firebaseAuth.currentUser ?: return
        if (user.isAnonymous) return
        val reminderId = reminder.id ?: return
        val docRef = firestore.collection(FirestorePaths.COLLECTION_USERS)
            .document(user.uid)
            .collection(FirestorePaths.COLLECTION_RUNNING_REMINDERS)
            .document(reminderId.toString())
        val data = mapOf(
            "hour" to reminder.hour,
            "minute" to reminder.minute,
            "enabled" to reminder.isEnabled,
            "days" to reminder.days.toList()
        )
        try {
            docRef.set(data).awaitResult()
        } catch (_: Exception) {
        }
    }

    private suspend fun deleteReminderRemoteIfNeeded(reminderId: Int) {
        val user = firebaseAuth.currentUser ?: return
        if (user.isAnonymous) return
        val docRef = firestore.collection(FirestorePaths.COLLECTION_USERS)
            .document(user.uid)
            .collection(FirestorePaths.COLLECTION_RUNNING_REMINDERS)
            .document(reminderId.toString())
        try {
            docRef.delete().awaitResult()
        } catch (_: Exception) {
        }
    }
}
