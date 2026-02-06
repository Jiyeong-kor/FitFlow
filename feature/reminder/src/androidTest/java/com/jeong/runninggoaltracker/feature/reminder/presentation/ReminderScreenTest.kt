package com.jeong.runninggoaltracker.feature.reminder.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.jeong.runninggoaltracker.feature.reminder.R
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import java.util.Calendar
import org.junit.Rule
import org.junit.Test

class ReminderScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun edit_flow_updates_list_after_save() {
        val initialReminder = ReminderUiState(
            id = 1,
            hour = 6,
            minute = 30,
            isEnabled = true,
            days = setOf(Calendar.MONDAY)
        )
        val uiState = mutableStateOf(ReminderListUiState(reminders = listOf(initialReminder)))
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val listItemLabel = context.getString(R.string.reminder_list_item_accessibility)
        val saveLabel = context.getString(R.string.reminder_action_save)
        val tuesdayLabel = context.getString(R.string.day_tue)

        composeRule.setContent {
            RunningGoalTrackerTheme {
                ReminderScreen(
                    state = uiState.value,
                    onAddClick = {},
                    onReminderClick = { id ->
                        val reminder = uiState.value.reminders.first { it.id == id }
                        uiState.value = uiState.value.copy(
                            viewMode = ReminderViewMode.EDIT,
                            editingReminder = EditingReminderState(
                                id = reminder.id,
                                hour = reminder.hour,
                                minute = reminder.minute,
                                isEnabled = reminder.isEnabled,
                                days = reminder.days
                            )
                        )
                    },
                    onDeleteReminder = {},
                    onToggleReminder = { _, _ -> },
                    onCancelEdit = {},
                    onSaveEdit = {
                        val editingReminder = uiState.value.editingReminder ?: return@ReminderScreen
                        uiState.value = uiState.value.copy(
                            viewMode = ReminderViewMode.LIST,
                            reminders = uiState.value.reminders.map { reminder ->
                                if (reminder.id == editingReminder.id) {
                                    reminder.copy(
                                        hour = editingReminder.hour,
                                        minute = editingReminder.minute,
                                        isEnabled = editingReminder.isEnabled,
                                        days = editingReminder.days
                                    )
                                } else {
                                    reminder
                                }
                            },
                            editingReminder = null
                        )
                    },
                    onDeleteEdit = {},
                    onToggleEditingEnabled = { isEnabled ->
                        uiState.value = uiState.value.copy(
                            editingReminder = uiState.value.editingReminder?.copy(isEnabled = isEnabled)
                        )
                    },
                    onUpdateEditingTime = { hour, minute ->
                        uiState.value = uiState.value.copy(
                            editingReminder = uiState.value.editingReminder?.copy(
                                hour = hour,
                                minute = minute
                            )
                        )
                    },
                    onToggleEditingDay = { day ->
                        val current = uiState.value.editingReminder ?: return@ReminderScreen
                        val updatedDays = if (current.days.contains(day)) {
                            current.days - day
                        } else {
                            current.days + day
                        }
                        uiState.value = uiState.value.copy(
                            editingReminder = current.copy(days = updatedDays)
                        )
                    },
                    onOpenTimePicker = {},
                    onDismissTimePicker = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription(listItemLabel).performClick()
        composeRule.onNodeWithText(tuesdayLabel).performClick()
        composeRule.onNodeWithContentDescription(saveLabel).performClick()
        composeRule.onNodeWithText(tuesdayLabel).assertIsDisplayed()
    }
}
