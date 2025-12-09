package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.repository.RunningRepository

class UpsertRunningGoalUseCase(
    private val repository: RunningRepository
) {
    suspend operator fun invoke(weeklyGoalKm: Double) {
        repository.upsertGoal(
            RunningGoal(weeklyGoalKm = weeklyGoalKm)
        )
    }
}
