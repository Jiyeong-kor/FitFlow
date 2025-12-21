package com.jeong.runninggoaltracker.feature.reminder.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ReminderScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun shows_reminders_and_invokes_actions() {
        var addCalled = false
        var toggleCalled = false
        var toggledDay: Pair<Int, Int>? = null
        var deletedId: Int? = null

        val reminder = ReminderUiState(
            id = 1,
            hour = 6,
            minute = 30,
            enabled = false,
            days = setOf(java.util.Calendar.MONDAY)
        )

        composeRule.setContent {
            RunningGoalTrackerTheme {
                ReminderScreen(
                    state = ReminderListUiState(reminders = listOf(reminder)),
                    context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext,
                    onAddReminder = { addCalled = true },
                    onDeleteReminder = { deletedId = it },
                    onToggleReminder = { _, enabled -> toggleCalled = enabled },
                    onUpdateTime = { _, _, _ -> },
                    onToggleDay = { id, day -> toggledDay = id to day }
                )
            }
        }

        composeRule.onNodeWithText("알림 시간 선택: 06:30").assertIsDisplayed()
        composeRule.onNodeWithText("알림 추가").performClick()
        composeRule.onAllNodes(isToggleable()).onFirst().performClick()
        composeRule.onNodeWithText("월").performClick()
        composeRule.onNodeWithText("삭제").performClick()

        assertTrue(addCalled)
        assertTrue(toggleCalled)
        assertEquals(1 to java.util.Calendar.MONDAY, toggledDay)
        assertEquals(1, deletedId)
    }
}
