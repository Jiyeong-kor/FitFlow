package com.jeong.fitflow.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.fitflow.data.contract.FirestorePaths
import com.jeong.fitflow.data.contract.RunningReminderFirestoreFields
import com.jeong.fitflow.data.local.RunningReminderDao
import com.jeong.fitflow.data.util.awaitResult
import com.jeong.fitflow.data.local.toDomain
import com.jeong.fitflow.data.local.toEntity
import com.jeong.fitflow.domain.model.RunningReminder
import com.jeong.fitflow.domain.repository.RunningReminderRepository
import com.jeong.fitflow.domain.di.IoDispatcher
import com.jeong.fitflow.shared.logging.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val appLogger: AppLogger
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
        runCatching {
            docRef.set(data).awaitResult()
        }.onFailure(::handleRemoteSyncFailure)
    }

    private suspend fun deleteReminderRemoteIfNeeded(reminderId: Int) {
        val user = firebaseAuth.currentUser ?: return
        if (user.isAnonymous) return
        val docRef = firestore.collection(FirestorePaths.COLLECTION_USERS)
            .document(user.uid)
            .collection(FirestorePaths.COLLECTION_RUNNING_REMINDERS)
            .document(reminderId.toString())
        runCatching {
            docRef.delete().awaitResult()
        }.onFailure(::handleRemoteSyncFailure)
    }

    private fun handleRemoteSyncFailure(throwable: Throwable) {
        appLogger.warning(LOG_TAG, REMOTE_SYNC_FAILURE_MESSAGE, throwable)
    }

    private companion object {
        const val LOG_TAG = "RunningReminderRepo"
        const val REMOTE_SYNC_FAILURE_MESSAGE = "Remote sync failed; using local data"
    }
}
