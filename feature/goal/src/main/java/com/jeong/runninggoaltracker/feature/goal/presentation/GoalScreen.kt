package com.jeong.runninggoaltracker.feature.goal.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.jeong.runninggoaltracker.feature.goal.R
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
import com.jeong.runninggoaltracker.shared.designsystem.theme.appAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appBackgroundColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextMutedColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextPrimaryColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import java.util.Locale

@Composable
fun GoalRoute(
    onBack: () -> Unit,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    GoalScreen(
        state = state,
        onGoalChange = viewModel::onWeeklyGoalChanged,
        onSave = { viewModel.saveGoal(onBack) }
    )
}

@Composable
fun GoalScreen(
    state: GoalUiState,
    onGoalChange: (String) -> Unit,
    onSave: () -> Unit
) {
    val errorText = when (state.error) {
        GoalInputError.INVALID_NUMBER -> stringResource(R.string.error_enter_number)
        GoalInputError.NON_POSITIVE -> stringResource(R.string.error_enter_positive_value)
        null -> null
    }

    val goalDistance = state.weeklyGoalInput.toDoubleOrNull()
        ?: state.currentGoalKm
        ?: 0.0

    val onSaveThrottled = rememberThrottleClick(onClick = onSave)
    val accentColor = appAccentColor()
    val backgroundColor = appBackgroundColor()
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            stringResource(R.string.goal_title_weekly_distance),
            color = textMuted,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            GoalAdjustButton(icon = Icons.Default.Remove) {
                val nextValue = (goalDistance - 0.5).coerceAtLeast(0.0)
                onGoalChange(String.format(Locale.getDefault(), "%.1f", nextValue))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format(Locale.getDefault(), "%.1f", goalDistance),
                    color = textPrimary,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    stringResource(R.string.goal_unit_km),
                    color = accentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            GoalAdjustButton(icon = Icons.Default.Add) {
                val nextValue = goalDistance + 0.5
                onGoalChange(String.format(Locale.getDefault(), "%.1f", nextValue))
            }
        }

        if (errorText != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            stringResource(R.string.goal_preset_section_title),
            modifier = Modifier.align(Alignment.Start),
            color = textMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val presets = listOf(
                stringResource(R.string.goal_preset_light_walk) to 5.0,
                stringResource(R.string.goal_preset_basic_fitness) to 10.0,
                stringResource(R.string.goal_preset_health_maintain) to 20.0
            )
            presets.forEach { (label, value) ->
                PresetCard(label = label, isSelected = goalDistance == value) {
                    onGoalChange(String.format(Locale.getDefault(), "%.1f", value))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSaveThrottled,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                stringResource(R.string.goal_save_button),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GoalAdjustButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val textPrimary = appTextPrimaryColor()

    Surface(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = textPrimary)
        }
    }
}

@Composable
private fun PresetCard(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val accentColor = appAccentColor()
    val textPrimary = appTextPrimaryColor()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            accentColor
        ) else null
    ) {
        Text(
            label,
            modifier = Modifier.padding(16.dp),
            color = if (isSelected) accentColor else textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GoalScreenPreview() {
    val state = GoalUiState(
        currentGoalKm = 15.0,
        weeklyGoalInput = "15.0",
        error = null
    )

    RunningGoalTrackerTheme {
        GoalScreen(
            state = state,
            onGoalChange = {},
            onSave = {}
        )
    }
}
