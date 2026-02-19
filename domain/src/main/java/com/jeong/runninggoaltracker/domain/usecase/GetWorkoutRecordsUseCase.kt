package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.WorkoutRecord
import com.jeong.runninggoaltracker.domain.repository.WorkoutRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWorkoutRecordsUseCase @Inject constructor(
    private val repository: WorkoutRecordRepository
) {
    operator fun invoke(): Flow<List<WorkoutRecord>> =
        repository.getAllRecords().map { records ->
            records.sortedByDescending { it.date }
        }
}
