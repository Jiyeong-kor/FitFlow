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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jeong.runninggoaltracker.feature.record.R
import com.jeong.runninggoaltracker.feature.record.api.model.ActivityRecognitionStatus
import com.jeong.runninggoaltracker.feature.record.viewmodel.RecordViewModel
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
import com.jeong.runninggoaltracker.shared.designsystem.theme.appAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appBackgroundColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appOnAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSurfaceColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextMutedColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextPrimaryColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import com.jeong.runninggoaltracker.shared.designsystem.config.NumericResourceProvider
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun RecordRoute(
    onRequestTrackingPermissions: (onResult: (Boolean) -> Unit) -> Unit,
    onRequestActivityRecognitionPermission: (onResult: (Boolean) -> Unit) -> Unit,
    viewModel: RecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RecordScreen(
        uiState = uiState,
        onStartActivityRecognition = viewModel::startActivityRecognition,
        onStopActivityRecognition = viewModel::stopActivityRecognition,
        onPermissionDenied = viewModel::notifyPermissionDenied,
        onStartTracking = viewModel::startTracking,
        onStopTracking = viewModel::stopTracking,
        onTrackingPermissionDenied = viewModel::notifyTrackingPermissionDenied,
        onRequestActivityRecognitionPermission = onRequestActivityRecognitionPermission,
        onRequestTrackingPermissions = onRequestTrackingPermissions
    )
}

@Composable
fun RecordScreen(
    uiState: RecordUiState,
    onStartActivityRecognition: ((onPermissionRequired: () -> Unit) -> Unit),
    onStopActivityRecognition: () -> Unit,
    onPermissionDenied: () -> Unit,
    onStartTracking: ((onPermissionRequired: () -> Unit) -> Unit),
    onStopTracking: () -> Unit,
    onTrackingPermissionDenied: () -> Unit,
    onRequestActivityRecognitionPermission: (onResult: (Boolean) -> Unit) -> Unit,
    onRequestTrackingPermissions: (onResult: (Boolean) -> Unit) -> Unit
) {
    val displayLabel = uiState.activityStatus.toRecordLabel()
    val context = LocalContext.current
    val zeroLong = NumericResourceProvider.zeroLong(context)
    val zeroDouble = NumericResourceProvider.zeroDouble(context)
    val secondsPerMinute = integerResource(R.integer.record_seconds_per_minute)

    val startActivityRecognitionWithPermission: () -> Unit = {
        onStartActivityRecognition {
            onRequestActivityRecognitionPermission { granted ->
                if (granted) {
                    onStartActivityRecognition {}
                } else {
                    onPermissionDenied()
                }
            }
        }
    }

    val startTrackingWithPermission: () -> Unit = {
        onStartTracking {
            onRequestTrackingPermissions { granted ->
                if (granted) {
                    onStartTracking {}
                } else {
                    onTrackingPermissionDenied()
                }
            }
        }
    }

    val onPauseClick = rememberThrottleClick(onClick = {
        if (uiState.isTracking) {
            onStopActivityRecognition()
            onStopTracking()
        } else {
            startActivityRecognitionWithPermission()
            startTrackingWithPermission()
        }
    })

    val onStopClick = rememberThrottleClick(onClick = {
        onStopActivityRecognition()
        onStopTracking()
    })

    val distanceValue = stringResource(R.string.record_distance_format, uiState.distanceKm)
    val accentColor = appAccentColor()
    val backgroundColor = appBackgroundColor()
    val surfaceColor = appSurfaceColor()
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val onAccent = appOnAccentColor()
    val paceLabel = formatPace(
        distanceKm = uiState.distanceKm,
        elapsedMillis = uiState.elapsedMillis,
        paceZero = stringResource(R.string.record_pace_zero),
        paceFormat = stringResource(R.string.record_pace_format),
        zeroDouble = zeroDouble,
        zeroLong = zeroLong,
        secondsPerMinute = secondsPerMinute
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(220.dp),
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    accentColor.copy(alpha = 0.3f)
                )
            ) {}

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    displayLabel.uppercase(Locale.getDefault()),
                    color = accentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    distanceValue,
                    color = textPrimary,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.record_unit_kilometers),
                    color = textMuted,
                    fontSize = 14.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricItem(
                label = stringResource(R.string.record_metric_time),
                value = formatElapsedTimeLabel(uiState.elapsedMillis, zeroLong),
                modifier = Modifier.weight(1f)
            )
            MetricItem(
                label = stringResource(R.string.record_metric_pace),
                value = paceLabel,
                modifier = Modifier.weight(1f)
            )
            MetricItem(
                label = stringResource(R.string.record_metric_calories),
                value = stringResource(R.string.record_calories_zero),
                modifier = Modifier.weight(1f)
            )
        }

        if (uiState.permissionRequired) {
            Text(
                text = stringResource(R.string.record_tracking_permission_required),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RecordControlButton(
                label = if (uiState.isTracking) {
                    stringResource(R.string.record_action_pause)
                } else {
                    stringResource(R.string.record_action_start)
                },
                icon = Icons.Default.Pause,
                containerColor = if (uiState.isTracking) surfaceColor else accentColor,
                contentColor = if (uiState.isTracking) textPrimary else onAccent,
                modifier = Modifier.weight(1f),
                onClick = onPauseClick
            )
            RecordControlButton(
                label = stringResource(R.string.record_action_stop),
                icon = Icons.Default.Stop,
                containerColor = Color(0xFFE53935),
                contentColor = onAccent,
                modifier = Modifier.weight(1f),
                onClick = onStopClick
            )
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = appTextMutedColor(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            color = appTextPrimaryColor(),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun RecordControlButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ActivityRecognitionStatus.toRecordLabel(): String {
    return when (this) {
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
}

private fun formatPace(
    distanceKm: Double,
    elapsedMillis: Long,
    paceZero: String,
    paceFormat: String,
    zeroDouble: Double,
    zeroLong: Long,
    secondsPerMinute: Int
): String {
    if (distanceKm <= zeroDouble || elapsedMillis <= zeroLong) {
        return paceZero
    }
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis)
    val secondsPerKm = totalSeconds.toDouble() / distanceKm
    val minutes = (secondsPerKm / secondsPerMinute).toInt()
    val seconds = (secondsPerKm % secondsPerMinute).toInt()
    return String.format(Locale.getDefault(), paceFormat, minutes, seconds)
}

@Composable
private fun formatElapsedTimeLabel(elapsedMillis: Long, zeroLong: Long): String {
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis)
    val hours = TimeUnit.SECONDS.toHours(totalSeconds)
    val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) - TimeUnit.HOURS.toMinutes(hours)
    val seconds = totalSeconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(totalSeconds))
    return if (hours > zeroLong) {
        stringResource(R.string.record_elapsed_time_hms_format, hours, minutes, seconds)
    } else {
        stringResource(R.string.record_elapsed_time_ms_format, minutes, seconds)
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordScreenPreview() {
    val uiState = RecordUiState(
        activityStatus = ActivityRecognitionStatus.Running,
        isTracking = true,
        distanceKm = 3.45,
        elapsedMillis = 1_245_000,
        permissionRequired = false
    )

    RunningGoalTrackerTheme {
        RecordScreen(
            uiState = uiState,
            onStartActivityRecognition = { _ -> },
            onStopActivityRecognition = {},
            onPermissionDenied = {},
            onStartTracking = { _ -> },
            onStopTracking = {},
            onTrackingPermissionDenied = {},
            onRequestActivityRecognitionPermission = {},
            onRequestTrackingPermissions = {}
        )
    }
}
