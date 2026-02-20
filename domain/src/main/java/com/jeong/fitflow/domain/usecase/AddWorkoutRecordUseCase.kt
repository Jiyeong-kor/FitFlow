package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.ExerciseType
import com.jeong.fitflow.domain.model.WorkoutRecord
import com.jeong.fitflow.domain.repository.WorkoutRecordRepository
import javax.inject.Inject

class AddWorkoutRecordUseCase @Inject constructor(
    private val repository: WorkoutRecordRepository
) {
    suspend operator fun invoke(date: Long, exerciseType: ExerciseType, repCount: Int) {
        repository.upsertRecord(
            WorkoutRecord(
                date = date,
                exerciseType = exerciseType,
                repCount = repCount
            )
        )
    }
}
