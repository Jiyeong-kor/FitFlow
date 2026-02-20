package com.jeong.fitflow.feature.goal.presentation

import com.jeong.fitflow.domain.model.RunningGoal
import com.jeong.fitflow.domain.repository.RunningGoalRepository
import com.jeong.fitflow.domain.usecase.GetRunningGoalUseCase
import com.jeong.fitflow.domain.usecase.UpsertRunningGoalUseCase
import com.jeong.fitflow.domain.usecase.ValidateWeeklyGoalUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GoalStateHolderTest {

    @Test
    fun `initial goal updates input state`() = runTest {
        val repository = FakeRunningGoalRepository(RunningGoal(weeklyGoalKm = 10.0))
        val stateHolder = createStateHolder(backgroundScope, repository)

        stateHolder.uiState.launchIn(backgroundScope)

        val state = stateHolder.uiState.filter { it.weeklyGoalKmInput != null }.first()

        assertEquals(10.0, state.weeklyGoalKmInput!!, 0.0)
        assertEquals(10.0, state.currentGoalKm!!, 0.0)
    }

    @Test
    fun `saveGoal updates repository and reports success`() = runTest {
        val repository = FakeRunningGoalRepository(RunningGoal(weeklyGoalKm = 5.0))
        val stateHolder = createStateHolder(backgroundScope, repository)
        var successCalled = false

        stateHolder.uiState.launchIn(backgroundScope)

        stateHolder.uiState.filter { it.currentGoalKm != null }.first()

        stateHolder.onWeeklyGoalChanged(12.0)
        stateHolder.saveGoal { successCalled = true }

        val state = stateHolder.uiState.filter { it.currentGoalKm == 12.0 }.first()

        assertEquals(12.0, state.currentGoalKm!!, 0.0)
        assertTrue(successCalled)

        assertEquals(12.0, repository.getGoalValue()?.weeklyGoalKm!!, 0.0)
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

        fun getGoalValue(): RunningGoal? = goalFlow.value
    }
}
