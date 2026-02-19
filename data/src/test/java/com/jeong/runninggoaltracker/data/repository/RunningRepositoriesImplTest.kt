package com.jeong.runninggoaltracker.data.repository

import com.jeong.runninggoaltracker.data.local.RunningGoalDao
import com.jeong.runninggoaltracker.data.local.RunningGoalEntity
import com.jeong.runninggoaltracker.data.local.RunningRecordEntity
import com.jeong.runninggoaltracker.data.local.RunningRecordDao
import com.jeong.runninggoaltracker.data.local.RunningReminderDao
import com.jeong.runninggoaltracker.data.local.RunningReminderEntity
import com.jeong.runninggoaltracker.data.local.SyncOutboxDao
import com.jeong.runninggoaltracker.data.local.SyncOutboxEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.domain.util.DateProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RunningRepositoriesImplTest {

    private val fakeDaos = FakeRunningDaos()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dateProvider = object : DateProvider {
        override fun getTodayFlow(): Flow<Long> = MutableStateFlow(1720000000000L)

        override fun getToday(): Long = 1720000000000L

        override fun getStartOfWeek(timestamp: Long): Long = timestamp
    }
    private val firebaseAuth = mockk<FirebaseAuth> {
        every { currentUser } returns null
    }
    private val firestore = mockk<FirebaseFirestore>(relaxed = true)
    private val syncOutboxDao = mockk<SyncOutboxDao>(relaxed = true) {
        coEvery { getPending(any()) } returns emptyList<SyncOutboxEntity>()
    }
    private val recordRepository =
        RunningRecordRepositoryImpl(fakeDaos, syncOutboxDao, firebaseAuth, firestore, testDispatcher)
    private val goalRepository =
        RunningGoalRepositoryImpl(fakeDaos, firebaseAuth, firestore, testDispatcher)
    private val reminderRepository =
        RunningReminderRepositoryImpl(
            fakeDaos,
            firebaseAuth,
            firestore,
            dateProvider,
            testDispatcher
        )

    @Test
    fun `record repository exposes mapped records and inserts entities`() = runBlocking {
        val recordDate = 1717200000000L
        fakeDaos.records.value = listOf(
            RunningRecordEntity(
                id = 1L,
                date = recordDate,
                distanceKm = 4.2,
                durationMinutes = 25
            )
        )

        val records = recordRepository.getAllRecords().first()

        assertEquals(1, records.size)
        assertEquals(recordDate, records.first().date)
        assertEquals(4.2, records.first().distanceKm, 0.0)

        val newRecord = RunningRecord(
            id = 2L,
            date = 1717286400000L,
            distanceKm = 10.0,
            durationMinutes = 50
        )

        recordRepository.addRecord(newRecord)

        assertEquals(
            RunningRecordEntity(
                id = 2L,
                date = 1717286400000L,
                distanceKm = 10.0,
                durationMinutes = 50
            ),
            fakeDaos.lastInsertedRecord
        )
    }

    @Test
    fun `goal repository maps latest goal and upserts entity`() = runBlocking {
        fakeDaos.goal.value = RunningGoalEntity(weeklyGoalKm = 15.5)

        val goal = goalRepository.getGoal().first()

        assertEquals(15.5, goal!!.weeklyGoalKm, 0.0)

        goalRepository.upsertGoal(RunningGoal(weeklyGoalKm = 20.0))

        assertEquals(RunningGoalEntity(weeklyGoalKm = 20.0), fakeDaos.lastUpsertedGoal)
        assertEquals(20.0, fakeDaos.goal.value!!.weeklyGoalKm, 0.0)
    }

    @Test
    fun `reminder repository maps reminders and coordinates dao updates`() = runBlocking {
        fakeDaos.reminders.value = listOf(
            RunningReminderEntity(
                id = 3,
                hour = 6,
                minute = 45,
                isEnabled = true,
                days = setOf(1, 5)
            )
        )

        val reminders = reminderRepository.getAllReminders().first()

        assertEquals(1, reminders.size)
        val reminder = reminders.first()
        assertEquals(setOf(1, 5), reminder.days)

        val newReminder = RunningReminder(
            id = null,
            hour = 7,
            minute = 15,
            isEnabled = true,
            days = setOf(2)
        )

        reminderRepository.upsertReminder(newReminder)
        reminderRepository.deleteReminder(3)

        assertTrue(
            fakeDaos.upsertedReminders.contains(
                RunningReminderEntity(
                    id = (dateProvider.getToday() % Int.MAX_VALUE).toInt(),
                    hour = 7,
                    minute = 15,
                    isEnabled = true,
                    days = setOf(2)
                )
            )
        )
        assertEquals(listOf(3), fakeDaos.deletedReminderIds)
        assertTrue(fakeDaos.reminders.value.none { it.id == 3 })
    }

    private class FakeRunningDaos : RunningRecordDao, RunningGoalDao, RunningReminderDao {
        val records = MutableStateFlow<List<RunningRecordEntity>>(emptyList())
        val goal = MutableStateFlow<RunningGoalEntity?>(null)
        val reminders = MutableStateFlow<List<RunningReminderEntity>>(emptyList())

        var lastInsertedRecord: RunningRecordEntity? = null
        var lastUpsertedGoal: RunningGoalEntity? = null
        val upsertedReminders = mutableListOf<RunningReminderEntity>()
        val deletedReminderIds = mutableListOf<Int>()

        override fun getAllRecords(): Flow<List<RunningRecordEntity>> = records

        override suspend fun insertRecord(record: RunningRecordEntity): Long {
            lastInsertedRecord = record
            records.value += record
            return record.id
        }


        override fun getGoal(): Flow<RunningGoalEntity?> = goal

        override suspend fun upsertGoal(goal: RunningGoalEntity) {
            lastUpsertedGoal = goal
            this.goal.value = goal
        }


        override fun getAllReminders(): Flow<List<RunningReminderEntity>> = reminders

        override suspend fun upsertReminder(reminder: RunningReminderEntity) {
            upsertedReminders += reminder
            reminders.value = reminders.value.filterNot { it.id == reminder.id } + reminder
        }

        override suspend fun deleteReminder(reminderId: Int) {
            deletedReminderIds += reminderId
            reminders.value = reminders.value.filterNot { it.id == reminderId }
        }

        override suspend fun countAll(): Int = records.value.size +
                (if (goal.value == null) 0 else 1) + reminders.value.size

        override suspend fun countByDate(dateMillis: Long): Int =
            records.value.count { it.date == dateMillis }
    }
}
