package com.jeong.fitflow.data.util

import com.google.firebase.firestore.DocumentSnapshot
import com.jeong.fitflow.data.contract.RunningGoalFirestoreFields
import com.jeong.fitflow.data.contract.RunningRecordFirestoreFields
import com.jeong.fitflow.data.contract.RunningReminderFirestoreFields
import com.jeong.fitflow.data.contract.WorkoutRecordFirestoreFields
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FirestoreDocumentMappersTest {

    @Test
    fun `러닝 기록 문서를 엔티티로 변환한다`() {
        val snapshot = mockk<DocumentSnapshot>()
        every { snapshot.id } returns "1"
        every { snapshot.getLong(RunningRecordFirestoreFields.DATE) } returns 1710460800000L
        every { snapshot.getDouble(RunningRecordFirestoreFields.DISTANCE_KM) } returns 5.2
        every { snapshot.getLong(RunningRecordFirestoreFields.DURATION_MINUTES) } returns 32L

        val entity = snapshot.toRunningRecordEntity()

        requireNotNull(entity)
        assertEquals(1L, entity.id)
        assertEquals(1710460800000L, entity.date)
        assertEquals(5.2, entity.distanceKm, 0.0)
        assertEquals(32, entity.durationMinutes)
    }

    @Test
    fun `러닝 알림 문서를 엔티티로 변환한다`() {
        val snapshot = mockk<DocumentSnapshot>()
        every { snapshot.id } returns "9"
        every { snapshot.getLong(RunningReminderFirestoreFields.HOUR) } returns 7L
        every { snapshot.getLong(RunningReminderFirestoreFields.MINUTE) } returns 30L
        every { snapshot.getBoolean(RunningReminderFirestoreFields.IS_ENABLED) } returns true
        every { snapshot.get(RunningReminderFirestoreFields.DAYS) } returns listOf(2, 4, 6)

        val entity = snapshot.toRunningReminderEntity()

        requireNotNull(entity)
        assertEquals(9, entity.id)
        assertEquals(7, entity.hour)
        assertEquals(30, entity.minute)
        assertEquals(true, entity.isEnabled)
        assertEquals(setOf(2, 4, 6), entity.days)
    }

    @Test
    fun `운동 기록 문서를 엔티티로 변환한다`() {
        val snapshot = mockk<DocumentSnapshot>()
        every { snapshot.getLong(WorkoutRecordFirestoreFields.DATE) } returns 1700000000000L
        every { snapshot.getString(WorkoutRecordFirestoreFields.EXERCISE_TYPE) } returns "SQUAT"
        every { snapshot.getLong(WorkoutRecordFirestoreFields.REP_COUNT) } returns 12L

        val entity = snapshot.toWorkoutRecordEntity()

        requireNotNull(entity)
        assertEquals(1700000000000L, entity.date)
        assertEquals("SQUAT", entity.exerciseType)
        assertEquals(12, entity.repCount)
    }

    @Test
    fun `주간 목표 문서를 엔티티로 변환한다`() {
        val snapshot = mockk<DocumentSnapshot>()
        every { snapshot.exists() } returns true
        every { snapshot.getDouble(RunningGoalFirestoreFields.WEEKLY_GOAL_KM) } returns 12.5

        val entity = snapshot.toRunningGoalEntity()

        requireNotNull(entity)
        assertEquals(12.5, entity.weeklyGoalKm, 0.0)
    }

    @Test
    fun `러닝 기록 문서 필드가 부족하면 null을 반환한다`() {
        val snapshot = mockk<DocumentSnapshot>()
        every { snapshot.id } returns ""

        val entity = snapshot.toRunningRecordEntity()

        assertNull(entity)
    }
}
