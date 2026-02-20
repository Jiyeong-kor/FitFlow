package com.jeong.fitflow.data.local

import com.jeong.fitflow.domain.model.ExerciseType
import com.jeong.fitflow.domain.model.RunningGoal
import com.jeong.fitflow.domain.model.RunningReminder
import com.jeong.fitflow.domain.model.WorkoutRecord
import org.junit.Assert.assertEquals
import org.junit.Test

class RunningMappersTest {

    @Test
    fun `러닝 기록 엔티티를 도메인 모델로 변환하고 다시 엔티티로 복원`() {
        val entity = RunningRecordEntity(
            id = 10L,
            date = 1710460800000L,
            distanceKm = 7.5,
            durationMinutes = 42
        )

        val domain = entity.toDomain()
        val mappedEntity = domain.toEntity()

        assertEquals(1710460800000L, domain.date)
        assertEquals(entity, mappedEntity)
    }

    @Test
    fun `주간 목표를 엔티티와 도메인 모델 간에 일관되게 매핑`() {
        val goal = RunningGoal(weeklyGoalKm = 12.0)

        val entity = goal.toEntity()
        val mappedGoal = entity.toDomain()

        assertEquals(goal, mappedGoal)
    }

    @Test
    fun `러닝 알림을 엔티티와 도메인 모델 간에 일관되게 매핑`() {
        val reminder = RunningReminder(
            id = 3,
            hour = 6,
            minute = 30,
            isEnabled = true,
            days = setOf(1, 6)
        )

        val entity = reminder.toEntity()
        val mappedReminder = entity.toDomain()

        assertEquals(reminder.copy(days = reminder.days), mappedReminder)
        assertEquals(setOf(1, 6), entity.days)
    }

    @Test
    fun `운동 기록을 엔티티와 도메인 모델 간에 매핑`() {
        val workoutRecord = WorkoutRecord(
            date = 1700000000000L,
            exerciseType = ExerciseType.SQUAT,
            repCount = 12
        )

        val entity = workoutRecord.toEntity()
        val mappedRecord = entity.toDomain()

        assertEquals(workoutRecord, mappedRecord)
        assertEquals(ExerciseType.SQUAT.name, entity.exerciseType)
    }
}
