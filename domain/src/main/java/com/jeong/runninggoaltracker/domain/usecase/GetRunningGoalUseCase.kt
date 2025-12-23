package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.repository.RunningGoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRunningGoalUseCase @Inject constructor(
    private val repository: RunningGoalRepository
) {
    operator fun invoke(): Flow<RunningGoal?> = repository.getGoal()
}
