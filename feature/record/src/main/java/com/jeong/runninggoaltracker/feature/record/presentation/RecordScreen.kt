package com.jeong.runninggoaltracker.feature.record.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.jeong.runninggoaltracker.feature.record.R
import com.jeong.runninggoaltracker.feature.record.contract.RECORD_PREVIEW_DISTANCE_HUNDREDTHS
import com.jeong.runninggoaltracker.feature.record.contract.RECORD_PREVIEW_DISTANCE_SCALE_HUNDREDTHS
import com.jeong.runninggoaltracker.feature.record.contract.RECORD_PREVIEW_ELAPSED_MILLIS
import com.jeong.runninggoaltracker.feature.record.contract.RECORD_WEIGHT_FULL
import com.jeong.runninggoaltracker.feature.record.api.model.ActivityRecognitionStatus
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
import com.jeong.runninggoaltracker.shared.designsystem.icon.AppIcons
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppAlphas
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppShapes
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppTypographyTokens
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import com.jeong.runninggoaltracker.shared.designsystem.theme.appAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appBackgroundColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appOnAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSurfaceColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextMutedColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextPrimaryColor
import java.util.Locale

@Composable
fun RecordScreen(
    uiState: RecordUiState,
    onStartActivityRecognition: () -> Unit,
    onStopActivityRecognition: () -> Unit,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit,
) {
    val displayLabel = uiState.activityStatus.toRecordLabel()
    val fullWeight = RECORD_WEIGHT_FULL

    val dimensions = LocalAppDimensions.current
    val typographyTokens = LocalAppTypographyTokens.current
    val alphas = LocalAppAlphas.current
    val onPauseClick = rememberThrottleClick(onClick = {
        if (uiState.isTracking) {
            onStopActivityRecognition()
            onStopTracking()
        } else {
            onStartActivityRecognition()
            onStartTracking()
        }
    })

    val onStopClick = rememberThrottleClick(onClick = {
        onStopActivityRecognition()
        onStopTracking()
    })

    val distanceValue = stringResource(R.string.record_distance_format, uiState.distanceKm)
    val accentColor = appAccentColor()
    val backgroundColor = appBackgroundColor()
    appSurfaceColor()
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    appOnAccentColor()
    val paceLabel = if (uiState.pace.isAvailable) {
        stringResource(
            R.string.record_pace_format,
            uiState.pace.minutes,
            uiState.pace.seconds
        )
    } else {
        stringResource(R.string.record_pace_zero)
    }
    val elapsedTimeLabel = if (uiState.elapsedTime.shouldShowHours) {
        stringResource(
            R.string.record_elapsed_time_hms_format,
            uiState.elapsedTime.hours,
            uiState.elapsedTime.minutes,
            uiState.elapsedTime.seconds
        )
    } else {
        stringResource(
            R.string.record_elapsed_time_ms_format,
            uiState.elapsedTime.minutes,
            uiState.elapsedTime.seconds
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(dimensions.recordScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(dimensions.recordTopSpacerHeight))

        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(dimensions.recordCircleSize),
                shape = CircleShape,
                color = accentColor.copy(alpha = alphas.recordAccentWeak),
                border = androidx.compose.foundation.BorderStroke(
                    dimensions.recordCircleBorder,
                    accentColor.copy(alpha = alphas.recordAccentStrong)
                )
            ) {}

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    displayLabel.uppercase(Locale.getDefault()),
                    color = accentColor,
                    style = typographyTokens.recordLabel
                )
                Spacer(
                    modifier = Modifier.height(
                        dimensions.recordLabelSpacerHeight
                    )
                )
                Text(
                    distanceValue,
                    color = textPrimary,
                    style = typographyTokens.displayLarge
                )
                Text(
                    stringResource(R.string.record_unit_kilometers),
                    color = textMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(dimensions.recordMetricSpacing)
        ) {
            MetricItem(
                label = stringResource(R.string.record_metric_time),
                value = elapsedTimeLabel,
                modifier = Modifier.weight(fullWeight)
            )
            MetricItem(
                label = stringResource(R.string.record_metric_pace),
                value = paceLabel,
                modifier = Modifier.weight(fullWeight)
            )
            MetricItem(
                label = stringResource(R.string.record_metric_calories),
                value = stringResource(R.string.record_calories_zero),
                modifier = Modifier.weight(fullWeight)
            )
        }

        if (uiState.isPermissionRequired) {
            Text(
                text = stringResource(R.string.record_tracking_permission_required),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensions.recordControlRowBottomPadding),
            horizontalArrangement =
                Arrangement.spacedBy(dimensions.recordMetricSpacing)
        ) {
            val startPauseIcon = if (uiState.isTracking) {
                AppIcons::pause
            } else {
                AppIcons::playArrow
            }

            RecordControlButton(
                label = if (uiState.isTracking) {
                    stringResource(R.string.record_action_pause)
                } else {
                    stringResource(R.string.record_action_start)
                },
                icon = startPauseIcon,
                variant = if (uiState.isTracking) {
                    RecordControlButtonVariant.SECONDARY
                } else {
                    RecordControlButtonVariant.PRIMARY
                },
                modifier = Modifier.weight(fullWeight),
                onClick = onPauseClick
            )
            RecordControlButton(
                label = stringResource(R.string.record_action_stop),
                icon = AppIcons::stop,
                variant = RecordControlButtonVariant.DESTRUCTIVE,
                modifier = Modifier.weight(fullWeight),
                onClick = onStopClick
            )
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, modifier: Modifier = Modifier) {
    val dimensions = LocalAppDimensions.current
    val appShapes = LocalAppShapes.current
    val typographyTokens = LocalAppTypographyTokens.current
    val alphas = LocalAppAlphas.current
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = alphas.recordMetricBackground),
                appShapes.roundedLg
            )
            .padding(dimensions.recordMetricItemPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            color = appTextMutedColor(),
            style = typographyTokens.labelTiny
        )
        Spacer(
            modifier = Modifier.height(
                dimensions.recordMetricLabelSpacing
            )
        )
        Text(
            value,
            color = appTextPrimaryColor(),
            style = typographyTokens.numericTitleMedium
        )
    }
}

@Composable
private fun RecordControlButton(
    label: String,
    icon: @Composable () -> androidx.compose.ui.graphics.painter.Painter,
    variant: RecordControlButtonVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalAppDimensions.current
    val appShapes = LocalAppShapes.current
    val onClickThrottled = rememberThrottleClick(onClick = onClick)
    val colors = when (variant) {
        RecordControlButtonVariant.PRIMARY -> {
            ButtonDefaults.buttonColors(
                containerColor = appAccentColor(),
                contentColor = appOnAccentColor()
            )
        }

        RecordControlButtonVariant.SECONDARY -> {
            ButtonDefaults.buttonColors(
                containerColor = appSurfaceColor(),
                contentColor = appTextPrimaryColor()
            )
        }

        RecordControlButtonVariant.DESTRUCTIVE -> {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = appOnAccentColor()
            )
        }
    }
    Button(
        onClick = onClickThrottled,
        modifier = modifier.height(dimensions.recordControlButtonHeight),
        colors = colors,
        shape = appShapes.roundedLg,
        contentPadding = PaddingValues(dimensions.sizeZero)
    ) {
        Icon(
            icon(),
            contentDescription = null,
            modifier = Modifier.size(dimensions.recordControlButtonIconSize)
        )
        Spacer(
            modifier = Modifier.width(
                dimensions.recordControlButtonIconSpacing
            )
        )
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

private enum class RecordControlButtonVariant {
    PRIMARY,
    SECONDARY,
    DESTRUCTIVE
}

@Composable
private fun ActivityRecognitionStatus.toRecordLabel(): String =
    when (this) {
        ActivityRecognitionStatus.NoPermission ->
            stringResource(R.string.activity_permission_needed)

        ActivityRecognitionStatus.RequestFailed,
        ActivityRecognitionStatus.SecurityException ->
            stringResource(R.string.activity_recognition_failed)

        ActivityRecognitionStatus.Stopped ->
            stringResource(R.string.activity_stopped)

        ActivityRecognitionStatus.NoResult,
        ActivityRecognitionStatus.NoActivity,
        ActivityRecognitionStatus.Unknown ->
            stringResource(R.string.activity_unknown)

        ActivityRecognitionStatus.Running ->
            stringResource(R.string.activity_running)

        ActivityRecognitionStatus.Walking ->
            stringResource(R.string.activity_walking)

        ActivityRecognitionStatus.OnBicycle ->
            stringResource(R.string.activity_on_bicycle)

        ActivityRecognitionStatus.InVehicle ->
            stringResource(R.string.activity_in_vehicle)

        ActivityRecognitionStatus.Still ->
            stringResource(R.string.activity_still)
    }

@Preview(showBackground = true)
@Composable
private fun RecordScreenPreview() {
    val distanceScale = RECORD_PREVIEW_DISTANCE_SCALE_HUNDREDTHS
    val previewDistance =
        RECORD_PREVIEW_DISTANCE_HUNDREDTHS.toDouble() / distanceScale
    val previewElapsedMillis = RECORD_PREVIEW_ELAPSED_MILLIS
    val uiState = RecordUiState(
        activityStatus = ActivityRecognitionStatus.Running,
        isTracking = true,
        distanceKm = previewDistance,
        elapsedMillis = previewElapsedMillis,
        isPermissionRequired = false
    )

    RunningGoalTrackerTheme {
        RecordScreen(
            uiState = uiState,
            onStartActivityRecognition = {},
            onStopActivityRecognition = {},
            onStartTracking = {},
            onStopTracking = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MetricItemPreview() = RunningGoalTrackerTheme {
    MetricItem(
        label = previewMetricLabel(),
        value = previewMetricValue()
    )
}

@Preview(showBackground = true)
@Composable
private fun RecordControlButtonPreview() = RunningGoalTrackerTheme {
    RecordControlButton(
        label = previewControlLabel(),
        icon = AppIcons::pause,
        variant = RecordControlButtonVariant.PRIMARY,
        onClick = {}
    )
}

@Composable
private fun previewMetricLabel(): String = stringResource(id = R.string.record_preview_metric_label)

@Composable
private fun previewMetricValue(): String = stringResource(id = R.string.record_preview_metric_value)

@Composable
private fun previewControlLabel(): String =
    stringResource(id = R.string.record_preview_control_label)
