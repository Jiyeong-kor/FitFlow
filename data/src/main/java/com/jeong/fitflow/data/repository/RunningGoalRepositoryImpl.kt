package com.jeong.fitflow.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.fitflow.data.contract.FirestorePaths
import com.jeong.fitflow.data.contract.RunningGoalFirestoreFields
import com.jeong.fitflow.data.local.RunningGoalDao
import com.jeong.fitflow.data.util.awaitResult
import com.jeong.fitflow.data.local.toDomain
import com.jeong.fitflow.data.local.toEntity
import com.jeong.fitflow.domain.model.RunningGoal
import com.jeong.fitflow.domain.repository.RunningGoalRepository
import com.jeong.fitflow.domain.di.IoDispatcher
import com.jeong.fitflow.shared.logging.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RunningGoalRepositoryImpl @Inject constructor(
    private val goalDao: RunningGoalDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val appLogger: AppLogger
) : RunningGoalRepository {

    override fun getGoal(): Flow<RunningGoal?> =
        goalDao.getGoal()
            .map { it?.toDomain() }
            .distinctUntilChanged()
            .flowOn(ioDispatcher)

    override suspend fun upsertGoal(goal: RunningGoal) {
        withContext(ioDispatcher) {
            goalDao.upsertGoal(goal.toEntity())
        }
        uploadGoalIfNeeded(goal)
    }

    private suspend fun uploadGoalIfNeeded(goal: RunningGoal) {
        val user = firebaseAuth.currentUser ?: return
        if (user.isAnonymous) return
        val docRef = firestore.collection(FirestorePaths.COLLECTION_USERS)
            .document(user.uid)
            .collection(FirestorePaths.COLLECTION_RUNNING_GOALS)
            .document(FirestorePaths.DOC_RUNNING_GOAL)
        val data = mapOf(RunningGoalFirestoreFields.WEEKLY_GOAL_KM to goal.weeklyGoalKm)
        try {
            docRef.set(data).awaitResult()
        } catch (exception: Exception) {
            handleRemoteSyncFailure(exception)
        }
    }

    private fun handleRemoteSyncFailure(exception: Exception) {
        appLogger.warning(
            tag = LOG_TAG,
            message = GOAL_SYNC_FAILURE_LOG,
            throwable = exception
        )
    }

    private companion object {
        const val LOG_TAG = "RunningGoalRepository"
        const val GOAL_SYNC_FAILURE_LOG = "Running goal remote sync failed"
    }
}
