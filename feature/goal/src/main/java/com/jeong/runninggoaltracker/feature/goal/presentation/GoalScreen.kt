package com.jeong.runninggoaltracker.feature.goal.presentation

import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.jeong.runninggoaltracker.feature.goal.R
import com.jeong.runninggoaltracker.feature.goal.contract.GOAL_PREVIEW_WEEKLY_GOAL_SCALE_TENTHS
import com.jeong.runninggoaltracker.feature.goal.contract.GOAL_PREVIEW_WEEKLY_GOAL_TENTHS
import com.jeong.runninggoaltracker.feature.goal.contract.GOAL_MIN_KM
import com.jeong.runninggoaltracker.feature.goal.contract.GOAL_PRESET_BASIC_FITNESS_KM
import com.jeong.runninggoaltracker.feature.goal.contract.GOAL_PRESET_HEALTH_MAINTAIN_KM
import com.jeong.runninggoaltracker.feature.goal.contract.GOAL_PRESET_LIGHT_WALK_KM
import com.jeong.runninggoaltracker.feature.goal.contract.GOAL_STEP_KM
import com.jeong.runninggoaltracker.feature.goal.contract.GOAL_WEIGHT_FULL
import com.jeong.runninggoaltracker.shared.designsystem.config.AppNumericTokens
import com.jeong.runninggoaltracker.shared.designsystem.formatter.DistanceFormatter
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
import com.jeong.runninggoaltracker.shared.designsystem.extension.throttleClick
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppAlphas
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppShapes
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppTypographyTokens
import com.jeong.runninggoaltracker.shared.designsystem.theme.appAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appBackgroundColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSurfaceColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextMutedColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextPrimaryColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import java.text.NumberFormat

@Composable
fun GoalScreen(
    state: GoalUiState,
    onGoalChange: (Double) -> Unit,
    onSave: () -> Unit
) {
    val errorText = when (state.error) {
        GoalInputError.INVALID_NUMBER -> stringResource(R.string.error_enter_number)
        GoalInputError.NON_POSITIVE -> stringResource(R.string.error_enter_positive_value)
        null -> null
    }

    val goalDistance = state.weeklyGoalKmInput
        ?: state.currentGoalKm
        ?: GOAL_MIN_KM
    val locale = LocalConfiguration.current.locales[0]
    val distanceFormatter = remember(locale) {
        DistanceFormatter(
            localeProvider = { locale },
            numberFormatFactory = NumberFormat::getNumberInstance
        )
    }
    val goalDistanceLabel = distanceFormatter.formatDistanceKm(
        distanceKm = goalDistance,
        fractionDigits = AppNumericTokens.DISTANCE_FRACTION_DIGITS
    )

    val onSaveThrottled = rememberThrottleClick(onClick = onSave)
    val onDecreaseThrottled = rememberThrottleClick {
        val nextValue = (goalDistance - GOAL_STEP_KM).coerceAtLeast(GOAL_MIN_KM)
        onGoalChange(nextValue)
    }
    val onIncreaseThrottled = rememberThrottleClick {
        val nextValue = goalDistance + GOAL_STEP_KM
        onGoalChange(nextValue)
    }
    val accentColor = appAccentColor()
    val backgroundColor = appBackgroundColor()
    val surfaceColor = appSurfaceColor()
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val dimensions = LocalAppDimensions.current
    val shapes = LocalAppShapes.current
    val typographyTokens = LocalAppTypographyTokens.current
    val screenPadding = dimensions.goalScreenPadding
    val titleTopSpacing = dimensions.goalTitleTopSpacing
    val titleBottomSpacing = dimensions.goalTitleBottomSpacing
    val adjustButtonSpacing = dimensions.goalAdjustButtonSpacing
    val errorSpacing = dimensions.goalErrorSpacing
    val presetSectionTopSpacing = dimensions.goalPresetSectionTopSpacing
    val presetSectionBottomSpacing = dimensions.goalPresetSectionBottomSpacing
    val presetItemSpacing = dimensions.goalPresetItemSpacing
    val saveButtonHeight = dimensions.goalSaveButtonHeight
    val fillWeight = GOAL_WEIGHT_FULL

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(titleTopSpacing))

        Text(
            stringResource(R.string.goal_title_weekly_distance),
            color = textMuted,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(titleBottomSpacing))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(adjustButtonSpacing)
        ) {
            GoalAdjustButton(
                icon = Icons.Default.Remove,
                contentDescription = stringResource(R.string.goal_action_decrease),
                onClick = onDecreaseThrottled
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = goalDistanceLabel,
                    color = textPrimary,
                    style = typographyTokens.displayLarge
                )
                Text(
                    stringResource(R.string.goal_unit_km),
                    color = accentColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            GoalAdjustButton(
                icon = Icons.Default.Add,
                contentDescription = stringResource(R.string.goal_action_increase),
                onClick = onIncreaseThrottled
            )
        }

        if (errorText != null) {
            Spacer(modifier = Modifier.height(errorSpacing))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(presetSectionTopSpacing))

        Text(
            stringResource(R.string.goal_preset_section_title),
            modifier = Modifier.align(Alignment.Start),
            color = textMuted,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(presetSectionBottomSpacing))

        Column(verticalArrangement = Arrangement.spacedBy(presetItemSpacing)) {
            val presets = listOf(
                stringResource(R.string.goal_preset_light_walk) to GOAL_PRESET_LIGHT_WALK_KM,
                stringResource(R.string.goal_preset_basic_fitness) to GOAL_PRESET_BASIC_FITNESS_KM,
                stringResource(R.string.goal_preset_health_maintain) to GOAL_PRESET_HEALTH_MAINTAIN_KM
            )
            presets.forEach { (label, value) ->
                PresetCard(label = label, isSelected = goalDistance == value) {
                    onGoalChange(value)
                }
            }
        }

        Spacer(modifier = Modifier.weight(fillWeight))

        Button(
            onClick = onSaveThrottled,
            modifier = Modifier
                .fillMaxWidth()
                .height(saveButtonHeight),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            shape = shapes.roundedLg
        ) {
            Text(
                stringResource(R.string.goal_save_button),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GoalAdjustButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val textPrimary = appTextPrimaryColor()
    val onClickThrottled = rememberThrottleClick(onClick = onClick)
    val dimensions = LocalAppDimensions.current
    val alphas = LocalAppAlphas.current
    val buttonSize = dimensions.goalAdjustButtonSize
    val borderWidth = dimensions.goalAdjustButtonBorderWidth
    val borderAlpha = alphas.goalBorder
    val transparentAlpha = alphas.transparent

    Surface(
        onClick = onClickThrottled,
        modifier = Modifier
            .size(buttonSize)
            .semantics { role = Role.Button },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = transparentAlpha),
        border = androidx.compose.foundation.BorderStroke(
            borderWidth,
            textPrimary.copy(alpha = borderAlpha)
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = contentDescription, tint = textPrimary)
        }
    }
}

@Composable
private fun PresetCard(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val accentColor = appAccentColor()
    val surfaceColor = appSurfaceColor()
    val textPrimary = appTextPrimaryColor()
    val dimensions = LocalAppDimensions.current
    val shapes = LocalAppShapes.current
    val alphas = LocalAppAlphas.current
    val borderWidth = dimensions.goalPresetCardBorderWidth
    val padding = dimensions.goalPresetCardPadding
    val selectedAlpha = alphas.goalSelectedBackground
    val unselectedAlpha = alphas.goalUnselectedBackground

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                role = Role.RadioButton
                selected = isSelected
            }
            .throttleClick(onClick = onClick),
        shape = shapes.roundedMd,
        color = if (isSelected) {
            accentColor.copy(alpha = selectedAlpha)
        } else {
            surfaceColor.copy(alpha = unselectedAlpha)
        },
        border = if (isSelected) androidx.compose.foundation.BorderStroke(
            borderWidth,
            accentColor
        ) else null
    ) {
        Text(
            label,
            modifier = Modifier.padding(padding),
            color = if (isSelected) accentColor else textPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GoalScreenPreview() {
    val previewScale = GOAL_PREVIEW_WEEKLY_GOAL_SCALE_TENTHS
    val previewGoalKm =
        GOAL_PREVIEW_WEEKLY_GOAL_TENTHS.toDouble() / previewScale
    val state = GoalUiState(
        currentGoalKm = previewGoalKm,
        weeklyGoalKmInput = previewGoalKm,
        error = null
    )

    RunningGoalTrackerTheme {
        GoalScreen(
            state = state,
            onGoalChange = { _ -> },
            onSave = {}
        )
    }
}
