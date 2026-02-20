package com.jeong.runninggoaltracker.feature.ai_coach.domain

import com.jeong.runninggoaltracker.domain.contract.SQUAT_INT_ZERO
import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.usecase.AddWorkoutRecordUseCase
import com.jeong.runninggoaltracker.domain.util.DateProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class WorkoutRecordSaver @Inject constructor(
    private val addWorkoutRecordUseCase: AddWorkoutRecordUseCase,
    private val dateProvider: DateProvider
) {
    private var lastSavedSnapshot: WorkoutSaveSnapshot? = null
    private val persistMutex: Mutex = Mutex()

    suspend fun persistIfNeeded(exerciseType: ExerciseType, repCount: Int) =
        persistMutex.withLock {
            if (repCount <= SQUAT_INT_ZERO) {
                return@withLock
            }
            val date = dateProvider.getToday()
            val snapshot = WorkoutSaveSnapshot(
                date = date,
                exerciseType = exerciseType,
                repCount = repCount
            )
            if (snapshot == lastSavedSnapshot) {
                return@withLock
            }
            lastSavedSnapshot = snapshot
            addWorkoutRecordUseCase(
                date = date,
                exerciseType = exerciseType,
                repCount = repCount
            )
        }
}

private data class WorkoutSaveSnapshot(
    val date: Long,
    val exerciseType: ExerciseType,
    val repCount: Int
)
