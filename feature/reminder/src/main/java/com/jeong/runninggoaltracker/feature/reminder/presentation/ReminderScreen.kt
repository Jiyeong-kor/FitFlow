package com.jeong.runninggoaltracker.feature.reminder.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.jeong.runninggoaltracker.feature.reminder.R
import com.jeong.runninggoaltracker.shared.designsystem.common.AppSurfaceCard
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
import com.jeong.runninggoaltracker.shared.designsystem.extension.throttleClick
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppAlphas
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppShapes
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppTypographyTokens
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import com.jeong.runninggoaltracker.shared.designsystem.theme.appAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appBackgroundColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appOnAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextMutedColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextPrimaryColor
import java.util.Calendar

@Composable
fun ReminderScreen(
    state: ReminderListUiState,
    onAddReminder: () -> Unit,
    onDeleteReminder: (Int) -> Unit,
    onToggleReminder: (Int, Boolean) -> Unit,
    onUpdateTime: (Int, Int, Int) -> Unit,
    onToggleDay: (Int, Int) -> Unit,
    onOpenTimePicker: (Int) -> Unit,
    onDismissTimePicker: () -> Unit
) {
    val messageHandler = rememberUserMessageHandler()
    val timeFormatter = rememberReminderTimeFormatter()
    val daysOfWeekLabelProvider = rememberDaysOfWeekLabelProvider()
    val onAddReminderThrottled = rememberThrottleClick(onClick = onAddReminder)
    val accentColor = appAccentColor()
    val backgroundColor = appBackgroundColor()
    val onAccentColor = appOnAccentColor()
    val textPrimary = appTextPrimaryColor()
    val dimensions = LocalAppDimensions.current
    val shapes = LocalAppShapes.current
    val typographyTokens = LocalAppTypographyTokens.current
    val paddingHorizontal = dimensions.reminderPaddingHorizontal
    val spacingMd = dimensions.reminderSpacingMd
    val spacingLg = dimensions.reminderSpacingLg
    val iconButtonSize = dimensions.reminderIconButtonSize
    val iconSize = dimensions.reminderIconSize

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = paddingHorizontal)
    ) {
        Spacer(modifier = Modifier.height(spacingMd))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.reminder_title_settings),
                color = textPrimary,
                style = typographyTokens.titleLargeAlt,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onAddReminderThrottled,
                modifier = Modifier
                    .background(accentColor, shapes.roundedSm)
                    .size(iconButtonSize)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.reminder_add_button_label),
                    tint = onAccentColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }

        Spacer(modifier = Modifier.height(spacingLg))

        LazyColumn(
            contentPadding = PaddingValues(bottom = spacingLg),
            verticalArrangement = Arrangement.spacedBy(spacingMd)
        ) {
            val list = state.reminders
            items(count = list.size, key = { index -> list[index].id }) { index ->
                ReminderCard(
                    reminder = list[index],
                    onToggleReminder = onToggleReminder,
                    onUpdateTime = onUpdateTime,
                    onToggleDay = onToggleDay,
                    onDeleteReminder = onDeleteReminder,
                    onOpenTimePicker = onOpenTimePicker,
                    onDismissTimePicker = onDismissTimePicker,
                    isTimePickerVisible = state.activeTimePickerId == list[index].id,
                    messageHandler = messageHandler,
                    timeFormatter = timeFormatter,
                    daysOfWeekLabelProvider = daysOfWeekLabelProvider
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderCard(
    reminder: ReminderUiState,
    onToggleReminder: (Int, Boolean) -> Unit,
    onUpdateTime: (Int, Int, Int) -> Unit,
    onToggleDay: (Int, Int) -> Unit,
    onDeleteReminder: (Int) -> Unit,
    onOpenTimePicker: (Int) -> Unit,
    onDismissTimePicker: () -> Unit,
    isTimePickerVisible: Boolean,
    messageHandler: UserMessageHandler,
    timeFormatter: ReminderTimeFormatter,
    daysOfWeekLabelProvider: DaysOfWeekLabelProvider
) {
    val accentColor = appAccentColor()
    val textMuted = appTextMutedColor()
    val textPrimary = appTextPrimaryColor()
    val dimensions = LocalAppDimensions.current
    val typographyTokens = LocalAppTypographyTokens.current
    val id = reminder.id
    val daysOfWeek = daysOfWeekLabelProvider.labels()
    val onDeleteReminderThrottled = rememberThrottleClick(onClick = { onDeleteReminder(id) })
    val timeLabel = timeFormatter.formatTime(reminder.hour, reminder.minute)
    val timePickerLabel = stringResource(
        R.string.reminder_time_picker_accessibility,
        timeLabel
    )
    val toggleLabel = stringResource(
        R.string.reminder_toggle_accessibility,
        timeLabel
    )
    val spacingMd = dimensions.reminderSpacingMd
    val daySpacing = dimensions.reminderSpacingSm
    val periodTextStyle = MaterialTheme.typography.labelSmall
    val timeTextStyle = typographyTokens.headlineLarge

    AppSurfaceCard(
        modifier = Modifier.fillMaxWidth(),
        isEnabled = reminder.isEnabled
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .semantics {
                            contentDescription = timePickerLabel
                            role = Role.Button
                        }
                        .throttleClick { onOpenTimePicker(id) }
                ) {
                    Text(
                        timeFormatter.periodLabel(reminder.hour),
                        color = textMuted,
                        style = periodTextStyle,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        timeFormatter.formatTime(reminder.hour, reminder.minute),
                        color = textPrimary,
                        style = timeTextStyle,
                        fontWeight = FontWeight.Black
                    )
                }
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = { isEnabled ->
                        if (isEnabled && reminder.days.isEmpty()) {
                            messageHandler.showMessage(
                                UiMessage(messageResId = R.string.reminder_error_select_at_least_one_day)
                            )
                        } else {
                            onToggleReminder(id, isEnabled)
                        }
                    },
                    modifier = Modifier.semantics {
                        contentDescription = toggleLabel
                        role = Role.Switch
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = accentColor,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(spacingMd))

            Row(horizontalArrangement = Arrangement.spacedBy(daySpacing)) {
                daysOfWeek.forEach { (dayInt, dayName) ->
                    val isSelected = reminder.days.contains(dayInt)
                    DayBubble(dayName, isSelected) {
                        onToggleDay(id, dayInt)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDeleteReminderThrottled) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.reminder_delete_button_label),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (isTimePickerVisible) {
        val timeState =
            rememberTimePickerState(initialHour = reminder.hour, initialMinute = reminder.minute)
        val onConfirmClick = rememberThrottleClick {
            onUpdateTime(id, timeState.hour, timeState.minute)
            onDismissTimePicker()
        }
        val onDismissClick = rememberThrottleClick {
            onDismissTimePicker()
        }
        AlertDialog(
            onDismissRequest = onDismissClick,
            confirmButton = {
                TextButton(onClick = onConfirmClick) { Text(stringResource(R.string.button_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = onDismissClick) { Text(stringResource(R.string.button_cancel)) }
            },
            title = { Text(stringResource(R.string.reminder_add_button_label)) },
            text = { TimeInput(state = timeState) }
        )
    }
}

@Composable
private fun DayBubble(day: String, isSelected: Boolean, onClick: () -> Unit) {
    val accentColor = appAccentColor()
    val textMuted = appTextMutedColor()
    val dimensions = LocalAppDimensions.current
    val shapes = LocalAppShapes.current
    val alphas = LocalAppAlphas.current
    val bubbleSize = dimensions.reminderDayBubbleSize
    val selectedAlpha = alphas.reminderSelectedDayBackground
    val unselectedTextAlpha = alphas.reminderUnselectedDayText
    val transparentAlpha = alphas.transparent
    val stateLabel = stringResource(
        if (isSelected) R.string.reminder_day_selected else R.string.reminder_day_unselected
    )

    Box(
        modifier = Modifier
            .size(bubbleSize)
            .background(
                color = if (isSelected) accentColor.copy(alpha = selectedAlpha) else MaterialTheme.colorScheme.surface.copy(
                    alpha = transparentAlpha
                ),
                shape = shapes.roundedXs
            )
            .semantics {
                contentDescription = day
                stateDescription = stateLabel
                role = Role.Button
            }
            .throttleClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            day,
            color = if (isSelected) accentColor else textMuted.copy(alpha = unselectedTextAlpha),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReminderScreenPreview() {
    val state = ReminderListUiState(
        reminders = listOf(
            ReminderUiState(
                id = 1,
                hour = 6,
                minute = 30,
                isEnabled = true,
                days = setOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY)
            ),
            ReminderUiState(
                id = 2,
                hour = 20,
                minute = 0,
                isEnabled = false,
                days = emptySet()
            )
        )
    )
    RunningGoalTrackerTheme {
        ReminderScreen(
            state = state,
            onAddReminder = {},
            onDeleteReminder = {},
            onToggleReminder = { _, _ -> },
            onUpdateTime = { _, _, _ -> },
            onToggleDay = { _, _ -> },
            onOpenTimePicker = {},
            onDismissTimePicker = {}
        )
    }
}
