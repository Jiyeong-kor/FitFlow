package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.repository.RunningReminderRepository
import java.time.DayOfWeek
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class UpsertRunningReminderUseCaseTest {

    private val repository = FakeRunningReminderRepository()
    private val useCase = UpsertRunningReminderUseCase(repository)

    @Test
    fun `리마인더를 리포지토리에 위임`() = runBlocking {
        val reminder = RunningReminder(
            id = 10,
            hour = 6,
            minute = 30,
            enabled = true,
            days = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        )

        useCase(reminder)

        assertEquals(listOf(reminder), repository.saved)
    }

    private class FakeRunningReminderRepository : RunningReminderRepository {
        val saved = mutableListOf<RunningReminder>()
        private val reminders = MutableStateFlow<List<RunningReminder>>(emptyList())

        override fun getAllReminders(): Flow<List<RunningReminder>> = reminders

        override suspend fun upsertReminder(reminder: RunningReminder) {
            saved += reminder
            reminders.value = reminders.value + reminder
        }

        override suspend fun deleteReminder(reminderId: Int) {
            reminders.value = reminders.value.filterNot { it.id == reminderId }
        }
    }
}
