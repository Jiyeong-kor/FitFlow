package com.jeong.fitflow.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import com.jeong.fitflow.feature.home.R
import com.jeong.fitflow.shared.designsystem.icon.AppIcons
import com.jeong.fitflow.shared.designsystem.theme.appBackgroundColor
import com.jeong.fitflow.shared.designsystem.theme.appSpacingLg
import com.jeong.fitflow.shared.designsystem.theme.appSpacingMd
import com.jeong.fitflow.shared.designsystem.theme.appTextMutedColor
import com.jeong.fitflow.shared.designsystem.theme.appTextPrimaryColor
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeActivityLogsScreen(
    activities: List<HomeWorkoutLogUiModel>,
    onBack: () -> Unit
) {
    val backgroundColor = appBackgroundColor()
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
    val dateFormatter = remember { HomeActivityDateFormatter() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_activity_log_all_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = AppIcons.arrowBack(),
                            contentDescription = stringResource(R.string.home_action_back),
                            tint = textPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (activities.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(innerPadding)
                    .padding(horizontal = appSpacingLg()),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.home_empty_activity_log),
                    color = textMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(innerPadding)
                    .padding(horizontal = appSpacingLg()),
                verticalArrangement = Arrangement.spacedBy(appSpacingMd())
            ) {
                items(
                    items = activities,
                    key = { activity -> "${activity.id}-${activity.timestamp}-${activity.type}" }
                ) { activity ->
                    HomeActivityLogListItem(
                        activity = activity,
                        locale = locale,
                        dateFormatter = dateFormatter
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeActivityLogListItem(
    activity: HomeWorkoutLogUiModel,
    locale: Locale,
    dateFormatter: HomeActivityDateFormatter
) {
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val titleText = when (activity.type) {
        HomeWorkoutType.RUNNING -> stringResource(
            R.string.home_activity_title_format,
            stringResource(R.string.activity_running),
            stringResource(R.string.home_distance_km_format, activity.distanceKm)
        )

        HomeWorkoutType.SQUAT -> stringResource(
            R.string.home_activity_title_format,
            stringResource(R.string.home_activity_squat),
            stringResource(R.string.home_activity_count_format, activity.repCount)
        )

        HomeWorkoutType.LUNGE -> stringResource(
            R.string.home_activity_title_format,
            stringResource(R.string.home_activity_lunge),
            stringResource(R.string.home_activity_count_format, activity.repCount)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = appSpacingMd())
    ) {
        Text(
            text = titleText,
            color = textPrimary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = dateFormatter.format(activity.timestamp, locale),
            color = textMuted,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
