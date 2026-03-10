package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.WorkoutRecord
import com.jeong.fitflow.domain.repository.WorkoutRecordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetWorkoutRecordsUseCase @Inject constructor(
    private val repository: WorkoutRecordRepository,
) {
    operator fun invoke(): Flow<List<WorkoutRecord>> = repository.getAllRecords().map { records ->
        records.sortedByDescending { it.date }
    }
}
