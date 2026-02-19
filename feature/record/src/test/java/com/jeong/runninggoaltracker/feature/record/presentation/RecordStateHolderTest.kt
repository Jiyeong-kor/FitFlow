package com.jeong.runninggoaltracker.feature.record.presentation

import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.repository.RunningRecordRepository
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRecordsUseCase
import com.jeong.runninggoaltracker.domain.usecase.EstimateActivityCaloriesUseCase
import com.jeong.runninggoaltracker.domain.util.RunningMetricsCalculator
import com.jeong.runninggoaltracker.feature.record.api.ActivityRecognitionMonitor
import com.jeong.runninggoaltracker.feature.record.api.RunningTrackerMonitor
import com.jeong.runninggoaltracker.feature.record.api.model.ActivityState
import com.jeong.runninggoaltracker.feature.record.api.model.RunningTrackerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecordStateHolderTest {

    @Test
    fun `ui state reflects records and tracker state`() = runTest {
        val recordsFlow = MutableStateFlow(
            listOf(
                RunningRecord(
                    id = 1L,
                    date = 1700000000000L,
                    distanceKm = 2.0,
                    durationMinutes = 10
                )
            )
        )
        val repository = FakeRunningRecordRepository(recordsFlow)
        val activityMonitor = FakeActivityRecognitionMonitor(ActivityState())
        val trackerMonitor = FakeRunningTrackerMonitor(
            RunningTrackerState(
                isTracking = true,
                distanceKm = 2.0,
                elapsedMillis = 600000L,
                isPermissionRequired = false
            )
        )
        val stateHolder = createStateHolder(backgroundScope, repository, activityMonitor, trackerMonitor)

        runCurrent()

        val uiState = stateHolder.uiState.value

        assertEquals(1, uiState.records.size)
        assertEquals(true, uiState.isTracking)
        assertEquals(2.0, uiState.distanceKm, 0.0)
    }

    private fun createStateHolder(
        scope: kotlinx.coroutines.CoroutineScope,
        repository: RunningRecordRepository,
        activityMonitor: ActivityRecognitionMonitor,
        trackerMonitor: RunningTrackerMonitor
    ): RecordStateHolder =
        RecordStateHolder(
            scope = scope,
            getRunningRecordsUseCase = GetRunningRecordsUseCase(repository),
            activityRecognitionMonitor = activityMonitor,
            runningTrackerMonitor = trackerMonitor,
            uiStateMapper = RecordUiStateMapper(RunningMetricsCalculator(), EstimateActivityCaloriesUseCase())
        )

    private class FakeRunningRecordRepository(
        private val records: MutableStateFlow<List<RunningRecord>>
    ) : RunningRecordRepository {
        override fun getAllRecords(): Flow<List<RunningRecord>> = records

        override suspend fun addRecord(record: RunningRecord): Long {
            records.value = records.value + record
            return record.id
        }
    }

    private class FakeActivityRecognitionMonitor(
        initialState: ActivityState
    ) : ActivityRecognitionMonitor {
        override val activityState = MutableStateFlow(initialState)
        override val activityLogs = MutableStateFlow(emptyList<com.jeong.runninggoaltracker.feature.record.api.model.ActivityLogEntry>())
    }

    private class FakeRunningTrackerMonitor(
        initialState: RunningTrackerState
    ) : RunningTrackerMonitor {
        override val trackerState = MutableStateFlow(initialState)
    }
}
