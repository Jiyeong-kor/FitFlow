package com.jeong.runninggoaltracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.runninggoaltracker.data.contract.FirestorePaths
import com.jeong.runninggoaltracker.data.contract.RunningReminderFirestoreFields
import com.jeong.runninggoaltracker.data.local.RunningReminderDao
import com.jeong.runninggoaltracker.data.util.awaitResult
import com.jeong.runninggoaltracker.data.local.toDomain
import com.jeong.runninggoaltracker.data.local.toEntity
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.repository.RunningReminderRepository
import com.jeong.runninggoaltracker.domain.di.IoDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class RunningReminderRepositoryImpl @Inject constructor(
    private val reminderDao: RunningReminderDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
) : RunningReminderRepository {

    private val reminderWriteMutex = Mutex()

    override fun getAllReminders(): Flow<List<RunningReminder>> =
        reminderDao.getAllReminders().map { reminders ->
            reminders.map { it.toDomain() }
        }.distinctUntilChanged()
            .flowOn(ioDispatcher)

    override suspend fun upsertReminder(reminder: RunningReminder) {
        val reminderWithId = reminderWriteMutex.withLock {
            withContext(ioDispatcher) {
                ensureReminderId(reminder).also { reminderWithId ->
                    reminderDao.upsertReminder(reminderWithId.toEntity())
                }
            }
        }
        uploadReminderIfNeeded(reminderWithId)
    }

    override suspend fun deleteReminder(reminderId: Int) {
        withContext(ioDispatcher) {
            reminderDao.deleteReminder(reminderId)
        }
        deleteReminderRemoteIfNeeded(reminderId)
    }

    private suspend fun ensureReminderId(reminder: RunningReminder): RunningReminder =
        reminder.id?.let { reminder }
            ?: reminder.copy(id = reminderDao.getNextReminderId())

    private suspend fun uploadReminderIfNeeded(reminder: RunningReminder) {
        val user = firebaseAuth.currentUser ?: return
        if (user.isAnonymous) return
        val reminderId = reminder.id ?: return
        val docRef = firestore.collection(FirestorePaths.COLLECTION_USERS)
            .document(user.uid)
            .collection(FirestorePaths.COLLECTION_RUNNING_REMINDERS)
            .document(reminderId.toString())
        val data = mapOf(
            RunningReminderFirestoreFields.HOUR to reminder.hour,
            RunningReminderFirestoreFields.MINUTE to reminder.minute,
            RunningReminderFirestoreFields.IS_ENABLED to reminder.isEnabled,
            RunningReminderFirestoreFields.DAYS to reminder.days.toList()
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
