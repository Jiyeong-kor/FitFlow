package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.repository.RunningRecordRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRunningRecordsUseCaseTest {

    private val repository = FakeRunningRecordRepository()
    private val useCase = GetRunningRecordsUseCase(repository)

    @Test
    fun `날짜 내림차순으로 정렬된 기록을 반환`() = runBlocking {
        val recordOld = RunningRecord(
            id = 1,
            date = LocalDate.of(2024, 1, 1),
            distanceKm = 3.0,
            durationMinutes = 20
        )
        val recordNew = RunningRecord(
            id = 2,
            date = LocalDate.of(2024, 6, 1),
            distanceKm = 5.0,
            durationMinutes = 30
        )

        repository.setRecords(listOf(recordOld, recordNew))

        val result = useCase().first()

        assertEquals(listOf(recordNew, recordOld), result)
    }

    private class FakeRunningRecordRepository : RunningRecordRepository {
        private val records = MutableStateFlow<List<RunningRecord>>(emptyList())

        override fun getAllRecords(): Flow<List<RunningRecord>> = records

        override suspend fun addRecord(record: RunningRecord) {
            records.value = records.value + record
        }

        fun setRecords(value: List<RunningRecord>) {
            records.value = value
        }
    }
}
