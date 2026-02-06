package com.jeong.runninggoaltracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.runninggoaltracker.data.contract.FirestorePaths
import com.jeong.runninggoaltracker.data.local.RunningGoalDao
import com.jeong.runninggoaltracker.data.util.awaitResult
import com.jeong.runninggoaltracker.data.local.toDomain
import com.jeong.runninggoaltracker.data.local.toEntity
import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.repository.RunningGoalRepository
import com.jeong.runninggoaltracker.domain.di.IoDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RunningGoalRepositoryImpl @Inject constructor(
    private val goalDao: RunningGoalDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
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
        val data = mapOf("weeklyGoalKm" to goal.weeklyGoalKm)
        try {
            docRef.set(data).awaitResult()
        } catch (_: Exception) {
        }
    }
}
