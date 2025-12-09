package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.repository.RunningGoalRepository
import kotlinx.coroutines.flow.Flow

class GetRunningGoalUseCase(
    private val repository: RunningGoalRepository
) {
    operator fun invoke(): Flow<RunningGoal?> = repository.getGoal()
}
