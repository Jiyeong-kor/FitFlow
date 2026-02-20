package com.jeong.fitflow.domain.repository

import com.jeong.fitflow.domain.model.RunningGoal
import kotlinx.coroutines.flow.Flow

interface RunningGoalRepository {
    fun getGoal(): Flow<RunningGoal?>
    suspend fun upsertGoal(goal: RunningGoal)
}
