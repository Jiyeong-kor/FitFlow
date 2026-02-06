package com.jeong.runninggoaltracker.feature.goal.presentation

import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.repository.RunningGoalRepository
import com.jeong.runninggoaltracker.domain.usecase.GetRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpsertRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.ValidateWeeklyGoalUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GoalStateHolderTest {

    @Test
    fun `initial goal updates input state`() = runTest {
        val repository = FakeRunningGoalRepository(RunningGoal(weeklyGoalKm = 10.0))
        val stateHolder = createStateHolder(this, repository)

        advanceUntilIdle()

        val state = stateHolder.uiState.value
        assertEquals(10.0, state.weeklyGoalKmInput, 0.0)
        assertEquals(10.0, state.currentGoalKm, 0.0)
    }

    @Test
    fun `saveGoal updates repository and reports success`() = runTest {
        val repository = FakeRunningGoalRepository(RunningGoal(weeklyGoalKm = 5.0))
        val stateHolder = createStateHolder(this, repository)
        var successCalled = false

        stateHolder.onWeeklyGoalChanged(12.0)
        stateHolder.saveGoal { successCalled = true }

        advanceUntilIdle()

        val state = stateHolder.uiState.value
        assertEquals(12.0, state.currentGoalKm, 0.0)
        assertTrue(successCalled)
    }

    private fun createStateHolder(
        scope: kotlinx.coroutines.CoroutineScope,
        repository: RunningGoalRepository
    ): GoalStateHolder =
        GoalStateHolder(
            scope = scope,
            getRunningGoalUseCase = GetRunningGoalUseCase(repository),
            goalSaveHandler = GoalSaveHandler(
                upsertRunningGoalUseCase = UpsertRunningGoalUseCase(repository),
                validateWeeklyGoalUseCase = ValidateWeeklyGoalUseCase()
            )
        )

    private class FakeRunningGoalRepository(
        initialGoal: RunningGoal?
    ) : RunningGoalRepository {
        private val goalFlow = MutableStateFlow(initialGoal)

        override fun getGoal(): Flow<RunningGoal?> = goalFlow

        override suspend fun upsertGoal(goal: RunningGoal) {
            goalFlow.value = goal
        }
    }
}
