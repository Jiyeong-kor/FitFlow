package com.jeong.fitflow.feature.reminder.presentation

import com.jeong.fitflow.domain.model.RunningReminder
import com.jeong.fitflow.domain.repository.RunningReminderRepository
import com.jeong.fitflow.domain.usecase.CreateDefaultReminderUseCase
import com.jeong.fitflow.domain.usecase.GetRunningRemindersUseCase
import com.jeong.fitflow.domain.usecase.ToggleReminderDayUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderStateHolderTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onAddClick enters edit mode with default reminder`() = runTest {
        val repository = FakeRunningReminderRepository()
        val defaultUseCase = CreateDefaultReminderUseCase()
        val stateHolder = createStateHolder(backgroundScope, repository, defaultUseCase)

        backgroundScope.launch { stateHolder.uiState.collect {} }

        stateHolder.onAddClick()
        runCurrent()

        val state = stateHolder.uiState.value
        assertEquals(ReminderViewMode.EDIT, state.viewMode)
        assertEquals(defaultUseCase().hour, state.editingReminder?.hour)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onToggleEditingDay updates editing reminder days`() = runTest {
        val repository = FakeRunningReminderRepository()
        val defaultUseCase = CreateDefaultReminderUseCase()
        val stateHolder = createStateHolder(backgroundScope, repository, defaultUseCase)

        backgroundScope.launch { stateHolder.uiState.collect {} }

        stateHolder.onAddClick()
        runCurrent()
        stateHolder.onToggleEditingDay(1)
        runCurrent()

        val editing = stateHolder.uiState.value.editingReminder
        assertTrue(editing?.days?.contains(1) == true)
    }

    private fun createStateHolder(
        scope: kotlinx.coroutines.CoroutineScope,
        repository: RunningReminderRepository,
        createDefaultReminderUseCase: CreateDefaultReminderUseCase
    ): ReminderStateHolder =
        ReminderStateHolder(
            scope = scope,
            getRunningRemindersUseCase = GetRunningRemindersUseCase(repository),
            createDefaultReminderUseCase = createDefaultReminderUseCase,
            toggleReminderDayUseCase = ToggleReminderDayUseCase(),
            reminderUiStateMapper = ReminderUiStateMapper()
        )

    private class FakeRunningReminderRepository : RunningReminderRepository {
        private val reminders = MutableStateFlow<List<RunningReminder>>(emptyList())

        override fun getAllReminders(): Flow<List<RunningReminder>> = reminders

        override suspend fun upsertReminder(reminder: RunningReminder) {
            reminders.value += reminder
        }

        override suspend fun deleteReminder(reminderId: Int) {
            reminders.value = reminders.value.filterNot { it.id == reminderId }
        }
    }
}
