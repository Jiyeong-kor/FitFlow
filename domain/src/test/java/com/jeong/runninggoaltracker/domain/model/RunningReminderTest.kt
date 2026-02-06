package com.jeong.runninggoaltracker.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RunningReminderTest {
    @Test
    fun toggleDayAddsMissingDay() {
        val reminder = RunningReminder(
            id = 1,
            hour = 7,
            minute = 30,
            isEnabled = true,
            days = setOf(1, 2)
        )

        val updated = reminder.toggleDay(3)

        assertEquals(setOf(1, 2, 3), updated.days)
    }

    @Test
    fun toggleDayRemovesExistingDay() {
        val reminder = RunningReminder(
            id = 2,
            hour = 9,
            minute = 0,
            isEnabled = false,
            days = setOf(1, 2, 3)
        )

        val updated = reminder.toggleDay(2)

        assertEquals(setOf(1, 3), updated.days)
    }
}
