package com.jeong.runninggoaltracker.feature.ai_coach.domain

import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.WorkoutRecord
import com.jeong.runninggoaltracker.domain.repository.WorkoutRecordRepository
import com.jeong.runninggoaltracker.domain.usecase.AddWorkoutRecordUseCase
import com.jeong.runninggoaltracker.domain.util.DateProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutRecordSaverTest {

    @Test
    fun persistIfNeeded_saves_both_squat_and_lunge_records() = runBlocking {
        val repository = FakeWorkoutRecordRepository()
        val saver = WorkoutRecordSaver(
            addWorkoutRecordUseCase = AddWorkoutRecordUseCase(repository),
            dateProvider = FakeDateProvider(1_700_000_000_000L)
        )

        saver.persistIfNeeded(exerciseType = ExerciseType.SQUAT, repCount = 12)
        saver.persistIfNeeded(exerciseType = ExerciseType.LUNGE, repCount = 8)

        assertEquals(2, repository.saved.size)
        assertEquals(ExerciseType.SQUAT, repository.saved[0].exerciseType)
        assertEquals(12, repository.saved[0].repCount)
        assertEquals(ExerciseType.LUNGE, repository.saved[1].exerciseType)
        assertEquals(8, repository.saved[1].repCount)
    }

    @Test
    fun persistIfNeeded_saves_once_for_same_snapshot_under_concurrency() = runBlocking {
        val repository = FakeWorkoutRecordRepository()
        val saver = WorkoutRecordSaver(
            addWorkoutRecordUseCase = AddWorkoutRecordUseCase(repository),
            dateProvider = FakeDateProvider(1_700_000_000_000L)
        )

        repeat(50) {
            val workers = List(8) {
                launch {
                    saver.persistIfNeeded(exerciseType = ExerciseType.SQUAT, repCount = 10)
                }
            }
            workers.joinAll()
        }

        assertEquals(1, repository.saved.size)
        assertEquals(ExerciseType.SQUAT, repository.saved.single().exerciseType)
        assertEquals(10, repository.saved.single().repCount)
    }

    private class FakeWorkoutRecordRepository : WorkoutRecordRepository {
        val saved = mutableListOf<WorkoutRecord>()

        override fun getAllRecords(): Flow<List<WorkoutRecord>> = emptyFlow()

        override suspend fun upsertRecord(record: WorkoutRecord) {
            saved += record
        }
    }

    private class FakeDateProvider(private val now: Long) : DateProvider {
        override fun getTodayFlow(): Flow<Long> = flowOf(now)

        override fun getToday(): Long = now

        override fun getStartOfWeek(timestamp: Long): Long = timestamp
    }
}
