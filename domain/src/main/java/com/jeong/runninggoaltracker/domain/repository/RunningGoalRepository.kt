package com.jeong.runninggoaltracker.domain.repository

import com.jeong.runninggoaltracker.domain.model.RunningGoal
import kotlinx.coroutines.flow.Flow

interface RunningGoalRepository {
    fun getGoal(): Flow<RunningGoal?>
    suspend fun upsertGoal(goal: RunningGoal)
}
