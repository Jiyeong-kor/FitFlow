package com.jeong.runninggoaltracker.feature.reminder.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.jeong.runninggoaltracker.shared.designsystem.common.AppTopBar
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
    onAddClick: () -> Unit,
    onReminderClick: (Int) -> Unit,
    onDeleteReminder: (Int) -> Unit,
    onToggleReminder: (Int, Boolean) -> Unit,
    onCancelEdit: () -> Unit,
    onSaveEdit: () -> Unit,
    onDeleteEdit: () -> Unit,
    onToggleEditingEnabled: (Boolean) -> Unit,
    onUpdateEditingTime: (Int, Int) -> Unit,
    onToggleEditingDay: (Int) -> Unit,
    onOpenTimePicker: () -> Unit,
    onDismissTimePicker: () -> Unit
) {
    val messageHandler = rememberUserMessageHandler()
    val timeFormatter = rememberReminderTimeFormatter()
    val daysOfWeekLabelProvider = rememberDaysOfWeekLabelProvider()

    when (state.viewMode) {
        ReminderViewMode.LIST -> ReminderListContent(
            reminders = state.reminders,
            onAddClick = onAddClick,
            onReminderClick = onReminderClick,
            onDeleteReminder = onDeleteReminder,
            onToggleReminder = onToggleReminder,
            messageHandler = messageHandler,
            timeFormatter = timeFormatter,
            daysOfWeekLabelProvider = daysOfWeekLabelProvider
        )
        ReminderViewMode.EDIT -> {
            val editingReminder = state.editingReminder
            if (editingReminder != null) {
                ReminderEditContent(
                    editingReminder = editingReminder,
                    onCancelEdit = onCancelEdit,
                    onSaveEdit = onSaveEdit,
                    onDeleteEdit = onDeleteEdit,
                    onToggleEditingEnabled = onToggleEditingEnabled,
                    onUpdateEditingTime = onUpdateEditingTime,
                    onToggleEditingDay = onToggleEditingDay,
                    onOpenTimePicker = onOpenTimePicker,
                    onDismissTimePicker = onDismissTimePicker,
                    messageHandler = messageHandler,
                    timeFormatter = timeFormatter,
                    daysOfWeekLabelProvider = daysOfWeekLabelProvider
                )
            }
        }
    }
}

@Composable
private fun ReminderListContent(
    reminders: List<ReminderUiState>,
    onAddClick: () -> Unit,
    onReminderClick: (Int) -> Unit,
    onDeleteReminder: (Int) -> Unit,
    onToggleReminder: (Int, Boolean) -> Unit,
    messageHandler: UserMessageHandler,
    timeFormatter: ReminderTimeFormatter,
    daysOfWeekLabelProvider: DaysOfWeekLabelProvider
) {
    val accentColor = appAccentColor()
    val backgroundColor = appBackgroundColor()
    val onAccentColor = appOnAccentColor()
    val textMuted = appTextMutedColor()
    val textPrimary = appTextPrimaryColor()
    val dimensions = LocalAppDimensions.current
    val typographyTokens = LocalAppTypographyTokens.current
    val paddingHorizontal = dimensions.reminderPaddingHorizontal
    val spacingSm = dimensions.reminderSpacingSm
    val spacingMd = dimensions.reminderSpacingMd
    val spacingLg = dimensions.reminderSpacingLg
    val iconButtonSize = dimensions.reminderIconButtonSize
    val iconSize = dimensions.reminderIconSize
    val onAddReminderThrottled = rememberThrottleClick(onClick = onAddClick)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ReminderTopBar(
            titleResId = null
        ) {
            IconButton(
                onClick = onAddReminderThrottled,
                modifier = Modifier
                    .background(accentColor, LocalAppShapes.current.roundedSm)
                    .size(iconButtonSize)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.reminder_action_add),
                    tint = onAccentColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }

        Spacer(modifier = Modifier.height(spacingMd))

        LazyColumn(
            modifier = Modifier.padding(horizontal = paddingHorizontal),
            contentPadding = PaddingValues(bottom = spacingLg),
            verticalArrangement = Arrangement.spacedBy(spacingMd)
        ) {
            items(count = reminders.size, key = { index -> reminders[index].id }) { index ->
                val reminder = reminders[index]
                val timeLabel = timeFormatter.formatTime(reminder.hour, reminder.minute)
                val dayLabels = daysOfWeekLabelProvider.labels()
                    .entries
                    .filter { reminder.days.contains(it.key) }
                    .map { it.value }
                val listItemLabel = stringResource(R.string.reminder_list_item_accessibility)
                val periodLabel = timeFormatter.periodLabel(reminder.hour)
                val toggleLabel = stringResource(R.string.reminder_toggle_accessibility, timeLabel)
                AppSurfaceCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = listItemLabel
                            role = Role.Button
                        }
                        .throttleClick { onReminderClick(reminder.id) },
                    isEnabled = reminder.isEnabled
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacingSm)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(spacingSm)) {
                                Text(
                                    text = periodLabel,
                                    style = typographyTokens.labelTiny,
                                    color = textMuted,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = timeLabel,
                                    style = typographyTokens.numericTitleLarge,
                                    color = textPrimary,
                                    fontWeight = FontWeight.Black
                                )
                                if (dayLabels.isNotEmpty()) {
                                    Text(
                                        text = dayLabels.joinToString(" "),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = textMuted
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacingSm)
                            ) {
                                Switch(
                                    checked = reminder.isEnabled,
                                    onCheckedChange = { isEnabled ->
                                        if (isEnabled && reminder.days.isEmpty()) {
                                            messageHandler.showMessage(
                                                UiMessage(
                                                    messageResId = R.string.reminder_error_select_at_least_one_day
                                                )
                                            )
                                        } else {
                                            onToggleReminder(reminder.id, isEnabled)
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
                                IconButton(
                                    onClick = rememberThrottleClick {
                                        onDeleteReminder(reminder.id)
                                    },
                                    modifier = Modifier.size(iconButtonSize)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.reminder_action_delete),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(iconSize)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderEditContent(
    editingReminder: EditingReminderState,
    onCancelEdit: () -> Unit,
    onSaveEdit: () -> Unit,
    onDeleteEdit: () -> Unit,
    onToggleEditingEnabled: (Boolean) -> Unit,
    onUpdateEditingTime: (Int, Int) -> Unit,
    onToggleEditingDay: (Int) -> Unit,
    onOpenTimePicker: () -> Unit,
    onDismissTimePicker: () -> Unit,
    messageHandler: UserMessageHandler,
    timeFormatter: ReminderTimeFormatter,
    daysOfWeekLabelProvider: DaysOfWeekLabelProvider
) {
    val accentColor = appAccentColor()
    val backgroundColor = appBackgroundColor()
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val dimensions = LocalAppDimensions.current
    val typographyTokens = LocalAppTypographyTokens.current
    val alphas = LocalAppAlphas.current
    val shapes = LocalAppShapes.current
    val spacingSm = dimensions.reminderSpacingSm
    val spacingMd = dimensions.reminderSpacingMd
    val spacingLg = dimensions.reminderSpacingLg
    val paddingHorizontal = dimensions.reminderPaddingHorizontal
    val timeLabel = timeFormatter.formatTime(editingReminder.hour, editingReminder.minute)
    val dayLabels = daysOfWeekLabelProvider.labels().entries
    val timeClickLabel = stringResource(R.string.reminder_time_edit_accessibility)
    val toggleLabel = stringResource(R.string.reminder_edit_switch_accessibility, timeLabel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        val titleResId = if (editingReminder.id == null) {
            R.string.reminder_add_title
        } else {
            R.string.reminder_edit_title
        }
        ReminderTopBar(
            titleResId = titleResId
        ) {
            IconButton(
                onClick = rememberThrottleClick(onClick = onCancelEdit),
                modifier = Modifier.size(dimensions.reminderIconButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.reminder_action_close),
                    tint = textPrimary,
                    modifier = Modifier.size(dimensions.reminderIconSize)
                )
            }
            IconButton(
                onClick = rememberThrottleClick(onClick = onSaveEdit),
                modifier = Modifier.size(dimensions.reminderIconButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.reminder_action_save),
                    tint = textPrimary,
                    modifier = Modifier.size(dimensions.reminderIconSize)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingHorizontal)
        ) {
            Spacer(modifier = Modifier.height(spacingLg))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = timeClickLabel
                        role = Role.Button
                    }
                    .throttleClick { onOpenTimePicker() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacingSm)
            ) {
                Text(
                    text = timeFormatter.periodLabel(editingReminder.hour),
                    style = typographyTokens.labelTiny,
                    color = textMuted,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = timeLabel,
                    style = typographyTokens.displayLarge,
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.height(spacingLg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.reminder_edit_enabled_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = textPrimary
                )
                Switch(
                    checked = editingReminder.isEnabled,
                    onCheckedChange = { isEnabled ->
                        if (isEnabled && editingReminder.days.isEmpty()) {
                            messageHandler.showMessage(
                                UiMessage(messageResId = R.string.reminder_error_select_at_least_one_day)
                            )
                        } else {
                            onToggleEditingEnabled(isEnabled)
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

            Spacer(modifier = Modifier.height(spacingLg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacingSm)
            ) {
                dayLabels.forEach { entry ->
                    val dayInt = entry.key
                    val dayName = entry.value
                    val isSelected = editingReminder.days.contains(dayInt)
                    DayChip(
                        label = dayName,
                        isSelected = isSelected,
                        selectedColor = accentColor,
                        mutedTextColor = textMuted,
                        selectedAlpha = alphas.reminderSelectedDayBackground,
                        unselectedTextAlpha = alphas.reminderUnselectedDayText,
                        onClick = { onToggleEditingDay(dayInt) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacingLg))

            Button(
                onClick = rememberThrottleClick(onClick = onDeleteEdit),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensions.goalSaveButtonHeight),
                shape = shapes.roundedLg,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(
                    text = stringResource(R.string.reminder_edit_delete_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (editingReminder.isTimePickerVisible) {
        val timeState = rememberTimePickerState(
            initialHour = editingReminder.hour,
            initialMinute = editingReminder.minute
        )
        val onConfirmClick = rememberThrottleClick {
            onUpdateEditingTime(timeState.hour, timeState.minute)
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
            title = { Text(stringResource(R.string.reminder_time_edit_accessibility)) },
            text = { TimeInput(state = timeState) }
        )
    }
}

@Composable
private fun DayChip(
    label: String,
    isSelected: Boolean,
    selectedColor: androidx.compose.ui.graphics.Color,
    mutedTextColor: androidx.compose.ui.graphics.Color,
    selectedAlpha: Float,
    unselectedTextAlpha: Float,
    onClick: () -> Unit
) {
    val dimensions = LocalAppDimensions.current
    val size = dimensions.reminderDayBubbleSize
    val stateLabel = stringResource(
        if (isSelected) R.string.reminder_day_selected else R.string.reminder_day_unselected
    )
    val contentLabel = stringResource(R.string.reminder_day_chip_accessibility, label)

    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = if (isSelected) {
                    selectedColor.copy(alpha = selectedAlpha)
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = LocalAppAlphas.current.transparent)
                },
                shape = CircleShape
            )
            .semantics {
                contentDescription = contentLabel
                stateDescription = stateLabel
                role = Role.Button
            }
            .throttleClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) {
                selectedColor
            } else {
                mutedTextColor.copy(alpha = unselectedTextAlpha)
            },
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ReminderTopBar(
    titleResId: Int?,
    actions: @Composable RowScope.() -> Unit
) {
    val dimensions = LocalAppDimensions.current
    Box(modifier = Modifier.fillMaxWidth()) {
        AppTopBar(titleResId = titleResId)
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = dimensions.reminderPaddingHorizontal),
            horizontalArrangement = Arrangement.spacedBy(dimensions.reminderSpacingSm),
            verticalAlignment = Alignment.CenterVertically,
            content = actions
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
            onAddClick = {},
            onReminderClick = {},
            onDeleteReminder = {},
            onToggleReminder = { _, _ -> },
            onCancelEdit = {},
            onSaveEdit = {},
            onDeleteEdit = {},
            onToggleEditingEnabled = {},
            onUpdateEditingTime = { _, _ -> },
            onToggleEditingDay = {},
            onOpenTimePicker = {},
            onDismissTimePicker = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReminderEditPreview() {
    val state = ReminderListUiState(
        viewMode = ReminderViewMode.EDIT,
        editingReminder = EditingReminderState(
            id = 1,
            hour = 6,
            minute = 30,
            isEnabled = true,
            days = setOf(Calendar.MONDAY, Calendar.WEDNESDAY)
        )
    )
    RunningGoalTrackerTheme {
        ReminderScreen(
            state = state,
            onAddClick = {},
            onReminderClick = {},
            onDeleteReminder = {},
            onToggleReminder = { _, _ -> },
            onCancelEdit = {},
            onSaveEdit = {},
            onDeleteEdit = {},
            onToggleEditingEnabled = {},
            onUpdateEditingTime = { _, _ -> },
            onToggleEditingDay = {},
            onOpenTimePicker = {},
            onDismissTimePicker = {}
        )
    }
}
