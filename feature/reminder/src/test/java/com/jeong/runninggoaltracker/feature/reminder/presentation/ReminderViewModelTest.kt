package com.jeong.runninggoaltracker.feature.reminder.presentation

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.usecase.CreateDefaultReminderUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRemindersUseCase
import com.jeong.runninggoaltracker.domain.usecase.ToggleReminderDayUseCase
import com.jeong.runninggoaltracker.domain.util.RunningReminderValidator
import com.jeong.runninggoaltracker.feature.reminder.alarm.ReminderSchedulingInteractor
import io.mockk.every
import io.mockk.mockk
import java.util.Calendar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReminderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun onReminderClick_entersEditMode() = runTest {
        val reminder = RunningReminder(
            id = 1,
            hour = 6,
            minute = 30,
            isEnabled = true,
            days = setOf(Calendar.MONDAY)
        )
        val getRemindersUseCase = mockk<GetRunningRemindersUseCase>()
        every { getRemindersUseCase() } returns flowOf(listOf(reminder))

        val viewModel = ReminderViewModel(
            getRunningRemindersUseCase = getRemindersUseCase,
            createDefaultReminderUseCase = mockk(relaxed = true),
            toggleReminderDayUseCase = mockk(relaxed = true),
            reminderSchedulingInteractor = mockk(relaxed = true),
            reminderUiStateMapper = ReminderUiStateMapper(),
            reminderUpdateHandler = ReminderUpdateHandler(
                reminderUiStateMapper = ReminderUiStateMapper(),
                reminderValidator = RunningReminderValidator()
            ),
            reminderValidator = RunningReminderValidator()
        )

        val collectJob = launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.onReminderClick(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ReminderViewMode.EDIT, state.viewMode)
        assertNotNull(state.editingReminder)
        assertEquals(1, state.editingReminder?.id)
        collectJob.cancel()
    }

    @Test
    fun onCancelEdit_returnsToListMode() = runTest {
        val reminder = RunningReminder(
            id = 2,
            hour = 7,
            minute = 0,
            isEnabled = false,
            days = setOf(Calendar.FRIDAY)
        )
        val getRemindersUseCase = mockk<GetRunningRemindersUseCase>()
        every { getRemindersUseCase() } returns flowOf(listOf(reminder))

        val viewModel = ReminderViewModel(
            getRunningRemindersUseCase = getRemindersUseCase,
            createDefaultReminderUseCase = mockk(relaxed = true),
            toggleReminderDayUseCase = mockk(relaxed = true),
            reminderSchedulingInteractor = mockk(relaxed = true),
            reminderUiStateMapper = ReminderUiStateMapper(),
            reminderUpdateHandler = ReminderUpdateHandler(
                reminderUiStateMapper = ReminderUiStateMapper(),
                reminderValidator = RunningReminderValidator()
            ),
            reminderValidator = RunningReminderValidator()
        )

        val collectJob = launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.onReminderClick(2)
        viewModel.onCancelEdit()

        val state = viewModel.uiState.value
        assertEquals(ReminderViewMode.LIST, state.viewMode)
        assertNull(state.editingReminder)
        collectJob.cancel()
    }

    @Test
    fun onAddClick_preparesDefaultEditState() = runTest {
        val defaultReminder = RunningReminder(
            id = null,
            hour = 8,
            minute = 0,
            isEnabled = false,
            days = emptySet()
        )
        val getRemindersUseCase = mockk<GetRunningRemindersUseCase>()
        every { getRemindersUseCase() } returns flowOf(emptyList())
        val createDefaultReminderUseCase = mockk<CreateDefaultReminderUseCase>()
        every { createDefaultReminderUseCase() } returns defaultReminder

        val viewModel = ReminderViewModel(
            getRunningRemindersUseCase = getRemindersUseCase,
            createDefaultReminderUseCase = createDefaultReminderUseCase,
            toggleReminderDayUseCase = mockk<ToggleReminderDayUseCase>(relaxed = true),
            reminderSchedulingInteractor = mockk<ReminderSchedulingInteractor>(relaxed = true),
            reminderUiStateMapper = ReminderUiStateMapper(),
            reminderUpdateHandler = ReminderUpdateHandler(
                reminderUiStateMapper = ReminderUiStateMapper(),
                reminderValidator = RunningReminderValidator()
            ),
            reminderValidator = RunningReminderValidator()
        )

        val collectJob = launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.onAddClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ReminderViewMode.EDIT, state.viewMode)
        assertEquals(8, state.editingReminder?.hour)
        assertEquals(0, state.editingReminder?.minute)
        collectJob.cancel()
    }
}
