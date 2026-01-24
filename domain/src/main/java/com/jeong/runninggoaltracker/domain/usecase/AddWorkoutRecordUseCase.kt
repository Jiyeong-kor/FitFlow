package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.WorkoutRecord
import com.jeong.runninggoaltracker.domain.repository.WorkoutRecordRepository
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
