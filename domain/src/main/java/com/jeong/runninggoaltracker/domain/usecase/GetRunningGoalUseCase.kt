package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.repository.RunningRepository
import kotlinx.coroutines.flow.Flow

class GetRunningGoalUseCase(
    private val repository: RunningRepository
) {
    operator fun invoke(): Flow<RunningGoal?> {
        return repository.getGoal()
    }
}
