package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.RunningGoal
import com.jeong.fitflow.domain.repository.RunningGoalRepository
import javax.inject.Inject

class UpsertRunningGoalUseCase @Inject constructor(
    private val repository: RunningGoalRepository
) {
    suspend operator fun invoke(goal: RunningGoal) {
        require(goal.weeklyGoalKm > 0.0)
        repository.upsertGoal(goal)
    }
}
