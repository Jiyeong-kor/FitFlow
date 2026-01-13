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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jeong.runninggoaltracker.feature.reminder.R
import com.jeong.runninggoaltracker.shared.designsystem.common.AppSurfaceCard
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
import com.jeong.runninggoaltracker.shared.designsystem.extension.throttleClick
import com.jeong.runninggoaltracker.shared.designsystem.theme.appAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appBackgroundColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSurfaceColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextMutedColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextPrimaryColor

@Composable
fun ReminderRoute(
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val userMessageHandler = rememberUserMessageHandler()
    val notificationPermissionRequester = rememberNotificationPermissionRequester {
        userMessageHandler.showMessage(
            UiMessage(messageResId = R.string.reminder_error_notification_permission_denied)
        )
    }
    val timeFormatter = rememberReminderTimeFormatter()
    val daysOfWeekLabelProvider = rememberDaysOfWeekLabelProvider()

    ReminderScreen(
        state = state,
        onAddReminder = viewModel::addReminder,
        onDeleteReminder = viewModel::deleteReminder,
        onToggleReminder = viewModel::updateEnabled,
        onUpdateTime = viewModel::updateTime,
        onToggleDay = viewModel::toggleDay,
        messageHandler = userMessageHandler,
        timeFormatter = timeFormatter,
        daysOfWeekLabelProvider = daysOfWeekLabelProvider,
        notificationPermissionRequester = notificationPermissionRequester
    )
}

@Composable
fun ReminderScreen(
    state: ReminderListUiState,
    onAddReminder: () -> Unit,
    onDeleteReminder: (Int) -> Unit,
    onToggleReminder: (Int, Boolean) -> Unit,
    onUpdateTime: (Int, Int, Int) -> Unit,
    onToggleDay: (Int, Int) -> Unit,
    messageHandler: UserMessageHandler,
    timeFormatter: ReminderTimeFormatter,
    daysOfWeekLabelProvider: DaysOfWeekLabelProvider,
    notificationPermissionRequester: NotificationPermissionRequester
) {
    val onAddReminderThrottled = rememberThrottleClick(onClick = onAddReminder)
    val accentColor = appAccentColor()
    val backgroundColor = appBackgroundColor()
    val textPrimary = appTextPrimaryColor()

    LaunchedEffect(Unit) {
        notificationPermissionRequester.requestPermissionIfNeeded()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.reminder_title_settings),
                color = textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onAddReminderThrottled,
                modifier = Modifier
                    .background(accentColor, RoundedCornerShape(12.dp))
                    .size(36.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val list = state.reminders.filter { it.id != null }
            items(count = list.size, key = { index -> list[index].id!! }) { index ->
                ReminderCard(
                    reminder = list[index],
                    onToggleReminder = onToggleReminder,
                    onUpdateTime = onUpdateTime,
                    onToggleDay = onToggleDay,
                    onDeleteReminder = onDeleteReminder,
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
    messageHandler: UserMessageHandler,
    timeFormatter: ReminderTimeFormatter,
    daysOfWeekLabelProvider: DaysOfWeekLabelProvider
) {
    val accentColor = appAccentColor()
    val surfaceColor = appSurfaceColor()
    val textMuted = appTextMutedColor()
    val textPrimary = appTextPrimaryColor()
    val showTimePicker = remember { mutableStateOf(false) }
    val id = reminder.id ?: return
    val daysOfWeek = daysOfWeekLabelProvider.labels()
    val onDeleteReminderThrottled = rememberThrottleClick(onClick = { onDeleteReminder(id) })

    AppSurfaceCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        containerColor = if (reminder.enabled) surfaceColor else Color.White.copy(alpha = 0.02f),
        contentPadding = PaddingValues(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.throttleClick { showTimePicker.value = true }) {
                    Text(
                        timeFormatter.periodLabel(reminder.hour),
                        color = textMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        timeFormatter.formatTime(reminder.hour, reminder.minute),
                        color = textPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Switch(
                    checked = reminder.enabled,
                    onCheckedChange = { enabled ->
                        if (enabled && reminder.days.isEmpty()) {
                            messageHandler.showMessage(
                                UiMessage(messageResId = R.string.reminder_error_select_at_least_one_day)
                            )
                            return@Switch
                        }
                        onToggleReminder(id, enabled)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = accentColor,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                daysOfWeek.forEach { (dayInt, dayName) ->
                    val isSelected = reminder.days.contains(dayInt)
                    DayBubble(dayName, isSelected && reminder.enabled) {
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
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showTimePicker.value) {
        val timeState =
            rememberTimePickerState(initialHour = reminder.hour, initialMinute = reminder.minute)
        val onConfirmClick = rememberThrottleClick {
            onUpdateTime(id, timeState.hour, timeState.minute)
            showTimePicker.value = false
        }
        val onDismissClick = rememberThrottleClick {
            showTimePicker.value = false
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

    Box(
        modifier = Modifier
            .size(32.dp)
            .background(
                color = if (isSelected) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .throttleClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            day,
            color = if (isSelected) accentColor else Color.Gray.copy(alpha = 0.5f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
