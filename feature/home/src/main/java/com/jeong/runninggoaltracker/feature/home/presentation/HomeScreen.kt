package com.jeong.runninggoaltracker.feature.home.presentation

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.jeong.runninggoaltracker.domain.model.PeriodState
import com.jeong.runninggoaltracker.feature.home.R
import com.jeong.runninggoaltracker.feature.home.contract.HOME_CALENDAR_COLUMN_COUNT
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_ACTIVITY_FIRST_DISTANCE_TENTHS
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_ACTIVITY_FIRST_DURATION_MINUTES
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_ACTIVITY_FIRST_ID
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_ACTIVITY_SECOND_COUNT
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_ACTIVITY_SECOND_DURATION_MINUTES
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_ACTIVITY_SECOND_ID
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_AVERAGE_PACE_MINUTES
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_AVERAGE_PACE_SECONDS
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_DAY_MILLIS
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_DISTANCE_SCALE_TENTHS
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_EPOCH_DAYS
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_TOTAL_CALORIES
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_TOTAL_DISTANCE_TENTHS
import com.jeong.runninggoaltracker.feature.home.contract.HOME_PREVIEW_TOTAL_DURATION_MINUTES
import com.jeong.runninggoaltracker.feature.home.contract.HOME_SUMMARY_ANIMATION_LABEL
import com.jeong.runninggoaltracker.feature.home.contract.HOME_WEIGHT_ONE
import com.jeong.runninggoaltracker.feature.home.contract.HomeTestTagContract
import com.jeong.runninggoaltracker.feature.home.domain.CalendarDay
import com.jeong.runninggoaltracker.feature.home.domain.CalendarMonthState
import com.jeong.runninggoaltracker.feature.home.domain.HomeCalendarCalculator
import com.jeong.runninggoaltracker.shared.designsystem.common.AppSurfaceCard
import com.jeong.runninggoaltracker.shared.designsystem.common.AppSurfaceCardTone
import com.jeong.runninggoaltracker.shared.designsystem.config.AppNumericTokens
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
import com.jeong.runninggoaltracker.shared.designsystem.extension.throttleClick
import com.jeong.runninggoaltracker.shared.designsystem.formatter.DistanceFormatter
import com.jeong.runninggoaltracker.shared.designsystem.icon.AppIcons
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppAlphas
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppShapes
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppTypographyTokens
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import com.jeong.runninggoaltracker.shared.designsystem.theme.appAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appBackgroundColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appOnAccentColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSpacingLg
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSpacingMd
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSpacingSm
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSurfaceColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextMutedColor
import com.jeong.runninggoaltracker.shared.designsystem.theme.appTextPrimaryColor
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onPeriodSelected: (PeriodState) -> Unit,
    onNavigatePreviousPeriod: () -> Unit,
    onNavigateNextPeriod: () -> Unit,
    onDateSelected: (Long) -> Unit,
    onCalendarOpen: () -> Unit,
    onCalendarDismiss: () -> Unit,
    onCalendarPreviousMonth: () -> Unit,
    onCalendarNextMonth: () -> Unit,
    onGoalClick: () -> Unit,
    onReminderClick: () -> Unit,
    onViewAllActivitiesClick: () -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val distanceFormatter = remember(locale) {
        DistanceFormatter(
            localeProvider = { locale },
            numberFormatFactory = NumberFormat::getNumberInstance
        )
    }
    val accentColor = appAccentColor()
    val backgroundColor = appBackgroundColor()
    appSurfaceColor()
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val dimensions = LocalAppDimensions.current
    val weightOne = HOME_WEIGHT_ONE
    val horizontalPadding = appSpacingLg()
    val minTouchTarget = dimensions.homeTouchTargetMin
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val goalDescription = uiState.weeklyGoalKm?.let { goalKm ->
        val formattedDistance = distanceFormatter.formatDistanceKm(
            distanceKm = goalKm,
            fractionDigits = AppNumericTokens.DISTANCE_FRACTION_DIGITS
        )
        stringResource(R.string.home_goal_summary_value, formattedDistance)
    } ?: stringResource(R.string.home_goal_summary_description)

    val onGoalClickThrottled = rememberThrottleClick(onClick = onGoalClick)
    val onReminderClickThrottled = rememberThrottleClick(onClick = onReminderClick)
    val onCalendarClickThrottled = rememberThrottleClick(onClick = onCalendarOpen)

    if (uiState.isCalendarVisible) {
        CalendarBottomSheet(
            selectedDateMillis = uiState.selectedDateState.dateMillis,
            calendarMonthState = uiState.calendarMonthState,
            calendarDays = uiState.calendarDays,
            onDateSelected = {
                onDateSelected(it)
                onCalendarDismiss()
            },
            onDismiss = onCalendarDismiss,
            onPreviousMonth = onCalendarPreviousMonth,
            onNextMonth = onCalendarNextMonth,
            sheetState = sheetState
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(appSpacingLg()),
        contentPadding = PaddingValues(top = appSpacingLg(), bottom = appSpacingLg())
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingMd())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingSm()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PeriodSegmentedTabs(
                        selectedPeriod = uiState.periodState,
                        onPeriodSelected = onPeriodSelected,
                        modifier = Modifier.weight(weightOne)
                    )
                    IconButton(onClick = onCalendarClickThrottled) {
                        Icon(
                            painter = AppIcons.calendar(),
                            contentDescription = stringResource(R.string.home_action_select_date),
                            tint = textPrimary
                        )
                    }
                }
                DateNavigator(
                    periodState = uiState.periodState,
                    selectedDateMillis = uiState.selectedDateState.dateMillis,
                    weeklyRange = uiState.weeklyRange,
                    onNavigatePreviousPeriod = onNavigatePreviousPeriod,
                    onNavigateNextPeriod = onNavigateNextPeriod
                )
            }
        }

        item {
            AnimatedContent(
                targetState = uiState.summary,
                label = HOME_SUMMARY_ANIMATION_LABEL
            ) { summary ->
                SummaryCard(
                    summary = summary,
                    accentColor = accentColor
                )
            }
        }

        item {
            SectionHeader(
                titleResId = R.string.home_section_activity_log,
                onViewAllClick = onViewAllActivitiesClick
            )
        }

        if (uiState.activityLogs.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.home_empty_activity_log),
                    color = textMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = appSpacingSm())
                )
            }
        } else {
            val visibleActivityLogs = uiState.activityLogs.take(5)
            items(
                items = visibleActivityLogs,
                key = { activity -> "${activity.id}-${activity.timestamp}-${activity.type}" }
            ) { activity ->
                ActivityLogRow(activity = activity)
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.home_section_weekly_manage),
                    color = textPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.home_action_reminder_settings),
                    color = accentColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable(onClick = onReminderClickThrottled)
                        .sizeIn(minWidth = minTouchTarget, minHeight = minTouchTarget)
                        .semantics { role = Role.Button }
                        .padding(horizontal = appSpacingSm(), vertical = appSpacingSm())
                )
            }
        }

        item {
            AppSurfaceCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) { role = Role.Button }
                    .clickable(onClick = onGoalClickThrottled)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(appSpacingSm())) {
                    Text(
                        text = stringResource(R.string.home_goal_summary_title),
                        color = textMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = goalDescription,
                        color = textPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PeriodSegmentedTabs(
    selectedPeriod: PeriodState,
    onPeriodSelected: (PeriodState) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        PeriodState.DAILY to R.string.home_tab_daily,
        PeriodState.WEEKLY to R.string.home_tab_weekly,
        PeriodState.MONTHLY to R.string.home_tab_monthly
    )
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, (period, labelResId) ->
            val onPeriodSelectedThrottled =
                rememberThrottleClick(onClick = { onPeriodSelected(period) })
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                selected = selectedPeriod == period,
                onClick = onPeriodSelectedThrottled
            ) {
                Text(text = stringResource(labelResId))
            }
        }
    }
}

@Composable
private fun DateNavigator(
    periodState: PeriodState,
    selectedDateMillis: Long,
    weeklyRange: HomeWeeklyRange,
    onNavigatePreviousPeriod: () -> Unit,
    onNavigateNextPeriod: () -> Unit
) {
    val label = periodLabel(periodState, selectedDateMillis, weeklyRange)
    val textPrimary = appTextPrimaryColor()
    val onPreviousClickThrottled = rememberThrottleClick(onClick = onNavigatePreviousPeriod)
    val onNextClickThrottled = rememberThrottleClick(onClick = onNavigateNextPeriod)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClickThrottled) {
            Icon(
                painter = AppIcons.arrowBack(),
                contentDescription = stringResource(R.string.home_action_previous_period),
                tint = textPrimary
            )
        }
        Text(
            text = label,
            color = textPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(onClick = onNextClickThrottled) {
            Icon(
                painter = AppIcons.arrowForward(),
                contentDescription = stringResource(R.string.home_action_next_period),
                tint = textPrimary
            )
        }
    }
}

@Composable
private fun SummaryCard(
    summary: HomeSummaryUiState,
    accentColor: androidx.compose.ui.graphics.Color
) {
    val typographyTokens = LocalAppTypographyTokens.current
    val textPrimary = MaterialTheme.colorScheme.onPrimaryContainer
    val textMuted = MaterialTheme.colorScheme.onPrimaryContainer
    AppSurfaceCard(tone = AppSurfaceCardTone.Emphasized) {
        Column(verticalArrangement = Arrangement.spacedBy(appSpacingMd())) {
            Text(
                text = stringResource(R.string.home_summary_title),
                color = textMuted,
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(appSpacingSm())
                ) {
                    Text(
                        text = stringResource(R.string.home_distance_value_format, summary.totalDistanceKm),
                        color = textPrimary,
                        style = typographyTokens.numericTitleLarge
                    )
                    Text(
                        text = stringResource(R.string.home_distance_unit_label),
                        color = textMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(
                            R.string.home_summary_squat_count,
                            summary.totalSquatCount
                        ),
                        color = textMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = stringResource(
                            R.string.home_summary_lunge_count,
                            summary.totalLungeCount
                        ),
                        color = textMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryMetricItem(
                    labelResId = R.string.home_metric_calories,
                    value = stringResource(
                        R.string.home_calories_kcal_format,
                        summary.totalCalories
                    ),
                    accentColor = accentColor,
                    textPrimary = textPrimary
                )
                SummaryMetricItem(
                    labelResId = R.string.home_metric_duration,
                    value = stringResource(
                        R.string.home_duration_min_format,
                        summary.totalDurationMinutes
                    ),
                    accentColor = accentColor,
                    textPrimary = textPrimary
                )
                SummaryMetricItem(
                    labelResId = R.string.home_metric_avg_speed,
                    value = speedLabel(
                        distanceKm = summary.totalDistanceKm,
                        durationMinutes = summary.totalDurationMinutes
                    ),
                    accentColor = accentColor,
                    textPrimary = textPrimary
                )
            }
        }
    }
}

@Composable
private fun SummaryMetricItem(
    @StringRes labelResId: Int,
    value: String,
    accentColor: androidx.compose.ui.graphics.Color,
    textPrimary: androidx.compose.ui.graphics.Color
) {
    val summaryMetricBackgroundAlpha = LocalAppAlphas.current.homeSummaryMetricBackground
    val shapes = LocalAppShapes.current
    val typographyTokens = LocalAppTypographyTokens.current

    Column(
        modifier = Modifier
            .background(
                accentColor.copy(alpha = summaryMetricBackgroundAlpha),
                shapes.roundedXs
            )
            .padding(horizontal = appSpacingMd(), vertical = appSpacingSm()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(labelResId),
            color = accentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            color = textPrimary,
            style = typographyTokens.numericBodyMedium
        )
    }
}

@Composable
private fun ActivityLogRow(
    activity: HomeWorkoutLogUiModel,
    modifier: Modifier = Modifier
) {
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val surfaceColor = appSurfaceColor()
    val dimensions = LocalAppDimensions.current
    val shapes = LocalAppShapes.current
    val weightOne = HOME_WEIGHT_ONE
    val iconPainter = when (activity.type) {
        HomeWorkoutType.RUNNING -> AppIcons.directionsRun()
        HomeWorkoutType.SQUAT -> AppIcons.fitnessCenter()
        HomeWorkoutType.LUNGE -> AppIcons.directionsWalk()
    }
    val dateLabel = activityDateLabel(activity.timestamp)
    val distanceLabel = when (activity.type) {
        HomeWorkoutType.RUNNING -> if (activity.distanceKm > 0) {
            stringResource(R.string.home_distance_km_format, activity.distanceKm)
        } else {
            stringResource(R.string.home_distance_placeholder)
        }

        HomeWorkoutType.SQUAT,
        HomeWorkoutType.LUNGE -> if (activity.repCount > 0) {
            stringResource(
                R.string.home_activity_count_format,
                activity.repCount
            )
        } else {
            stringResource(R.string.home_count_placeholder)
        }
    }
    val durationLabel = if (activity.durationMinutes > 0) {
        stringResource(R.string.home_duration_min_format, activity.durationMinutes)
    } else {
        stringResource(R.string.home_duration_placeholder)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceColor, shapes.roundedSm)
            .padding(appSpacingMd())
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(appSpacingMd()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.homeActivityLogIconSize)
                .background(surfaceColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = textMuted
            )
        }
        Column(modifier = Modifier.weight(weightOne)) {
            val titleText = stringResource(
                R.string.home_activity_title_format,
                stringResource(activityTypeLabelRes(activity.type)),
                distanceLabel
            )
            val subtitleText = if (activity.type == HomeWorkoutType.RUNNING) {
                stringResource(
                    R.string.home_activity_subtitle_format,
                    dateLabel,
                    durationLabel
                )
            } else {
                dateLabel
            }
            Text(
                text = titleText,
                color = textPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitleText,
                color = textMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@StringRes
private fun activityTypeLabelRes(type: HomeWorkoutType): Int =
    when (type) {
        HomeWorkoutType.RUNNING -> R.string.activity_running
        HomeWorkoutType.SQUAT -> R.string.home_activity_squat
        HomeWorkoutType.LUNGE -> R.string.home_activity_lunge
    }

@Composable
private fun SectionHeader(
    @StringRes titleResId: Int,
    onViewAllClick: () -> Unit
) {
    val accentColor = appAccentColor()
    val textPrimary = appTextPrimaryColor()
    val dimensions = LocalAppDimensions.current
    val minTouchTarget = dimensions.homeTouchTargetMin
    val onViewAllClickThrottled = rememberThrottleClick(onClick = onViewAllClick)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(titleResId),
            color = textPrimary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.home_action_view_all),
            color = accentColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable(onClick = onViewAllClickThrottled)
                .sizeIn(minWidth = minTouchTarget, minHeight = minTouchTarget)
                .semantics { role = Role.Button }
                .testTag(HomeTestTagContract.HOME_ACTIVITY_LOG_VIEW_ALL_TAG)
                .padding(horizontal = appSpacingSm(), vertical = appSpacingSm())
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun CalendarBottomSheet(
    selectedDateMillis: Long,
    calendarMonthState: CalendarMonthState,
    calendarDays: List<CalendarDay?>,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val accentColor = appAccentColor()
    val onAccentColor = appOnAccentColor()
    val dimensions = LocalAppDimensions.current
    val homeCalendarDotSize = dimensions.spacingXs
    val weightOne = HOME_WEIGHT_ONE
    val calendarColumnCount = HOME_CALENDAR_COLUMN_COUNT
    val onPreviousMonthClickThrottled = rememberThrottleClick(onClick = onPreviousMonth)
    val onNextMonthClickThrottled = rememberThrottleClick(onClick = onNextMonth)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        val calendarGridHeight = dimensions.homeCalendarGridHeight
        val calendarDaySize = dimensions.homeCalendarDaySize
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = appSpacingLg(), vertical = appSpacingMd()),
            verticalArrangement = Arrangement.spacedBy(appSpacingMd())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPreviousMonthClickThrottled
                ) {
                    Icon(
                        painter = AppIcons.arrowBack(),
                        contentDescription = stringResource(R.string.home_action_previous_month),
                        tint = textPrimary
                    )
                }
                Text(
                    text = yearMonthLabel(calendarMonthState),
                    color = textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = onNextMonthClickThrottled
                ) {
                    Icon(
                        painter = AppIcons.arrowForward(),
                        contentDescription = stringResource(R.string.home_action_next_month),
                        tint = textPrimary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                calendarDayOfWeekLabels().forEach { labelResId ->
                    Text(
                        text = stringResource(labelResId),
                        color = textMuted,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(weightOne),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(calendarColumnCount),
                horizontalArrangement = Arrangement.spacedBy(appSpacingSm()),
                verticalArrangement = Arrangement.spacedBy(appSpacingSm()),
                modifier = Modifier.height(calendarGridHeight),
                userScrollEnabled = false
            ) {
                items(calendarDays) { day ->
                    if (day == null) {
                        Spacer(modifier = Modifier.size(calendarDaySize))
                    } else {
                        val isSelected = day.isSameDay(selectedDateMillis)
                        val dayLabel = stringResource(
                            R.string.home_calendar_day_label,
                            day.dayOfMonth
                        )
                        val selectionState = stringResource(
                            if (isSelected) {
                                R.string.home_calendar_day_selected
                            } else {
                                R.string.home_calendar_day_unselected
                            }
                        )
                        Box(
                            modifier = Modifier
                                .size(calendarDaySize)
                                .background(
                                    color = if (isSelected) accentColor else appSurfaceColor(),
                                    shape = CircleShape
                                )
                                .semantics {
                                    contentDescription = dayLabel
                                    stateDescription = selectionState
                                    role = Role.Button
                                }
                                .throttleClick(
                                    onClick = { onDateSelected(day.timestampMillis) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(appSpacingSm())
                            ) {
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    color = if (isSelected) onAccentColor else textPrimary,
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Box(
                                    modifier = Modifier
                                        .size(homeCalendarDotSize)
                                        .background(
                                            color = if (day.hasActivity) {
                                                if (isSelected) onAccentColor else accentColor
                                            } else {
                                                accentColor.copy(alpha = 0f)
                                            },
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun periodLabel(
    periodState: PeriodState,
    selectedDateMillis: Long,
    weeklyRange: HomeWeeklyRange
): String {
    val locale = Locale.getDefault()
    val dayPattern = stringResource(R.string.home_date_format_day)
    val monthPattern = stringResource(R.string.home_date_format_month)
    val rangePattern = stringResource(R.string.home_date_format_range)
    val periodText = HomeUiFormatter.periodLabelText(
        periodState = periodState,
        selectedDateMillis = selectedDateMillis,
        weeklyRange = weeklyRange,
        dayPattern = dayPattern,
        monthPattern = monthPattern,
        rangePattern = rangePattern,
        locale = locale
    )
    return if (periodText.formatArgs.isEmpty()) {
        stringResource(periodText.resId)
    } else {
        stringResource(periodText.resId, *periodText.formatArgs.toTypedArray())
    }
}

@Composable
private fun speedLabel(distanceKm: Double, durationMinutes: Int): String {
    if (distanceKm <= 0.0 || durationMinutes <= 0) {
        return stringResource(R.string.home_speed_placeholder)
    }
    val speedKmPerHour = distanceKm / (durationMinutes / 60.0)
    return stringResource(R.string.home_speed_format, speedKmPerHour)
}

@Composable
private fun activityDateLabel(timestampMillis: Long): String {
    val locale = Locale.getDefault()
    val rangePattern = stringResource(R.string.home_date_format_range)
    return HomeUiFormatter.activityDateText(
        timestampMillis = timestampMillis,
        rangePattern = rangePattern,
        locale = locale
    )
}

@Composable
private fun yearMonthLabel(state: CalendarMonthState): String {
    val locale = Locale.getDefault()
    val monthPattern = stringResource(R.string.home_date_format_month)
    return HomeUiFormatter.yearMonthText(
        year = state.year,
        month = state.month,
        monthPattern = monthPattern,
        locale = locale
    )
}

private fun calendarDayOfWeekLabels(): List<Int> = listOf(
    R.string.home_day_of_week_sun,
    R.string.home_day_of_week_mon,
    R.string.home_day_of_week_tue,
    R.string.home_day_of_week_wed,
    R.string.home_day_of_week_thu,
    R.string.home_day_of_week_fri,
    R.string.home_day_of_week_sat
)


@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val calendarCalculator = remember { HomeCalendarCalculator() }
    val distanceScale = HOME_PREVIEW_DISTANCE_SCALE_TENTHS
    val totalDistance =
        HOME_PREVIEW_TOTAL_DISTANCE_TENTHS.toDouble() / distanceScale
    val firstActivityDistance =
        HOME_PREVIEW_ACTIVITY_FIRST_DISTANCE_TENTHS.toDouble() / distanceScale
    val secondActivityCount = HOME_PREVIEW_ACTIVITY_SECOND_COUNT
    val dayMillis = HOME_PREVIEW_DAY_MILLIS
    val epochDays = HOME_PREVIEW_EPOCH_DAYS
    val baseMillis = epochDays * dayMillis
    val calendarMonthState = calendarCalculator.monthStateFromMillis(baseMillis)
    val uiState = HomeUiState(
        periodState = PeriodState.WEEKLY,
        selectedDateState = SelectedDateState(dateMillis = baseMillis),
        calendarMonthState = calendarMonthState,
        calendarDays = calendarCalculator.buildCalendarDays(calendarMonthState),
        weeklyRange = HomeWeeklyRange(
            startMillis = baseMillis,
            endMillis = baseMillis
        ),
        summary = HomeSummaryUiState(
            totalDistanceKm = totalDistance,
            totalCalories = HOME_PREVIEW_TOTAL_CALORIES,
            totalDurationMinutes = HOME_PREVIEW_TOTAL_DURATION_MINUTES,
            averagePace = HomePaceUiState(
                minutes = HOME_PREVIEW_AVERAGE_PACE_MINUTES,
                seconds = HOME_PREVIEW_AVERAGE_PACE_SECONDS,
                isAvailable = true
            )
        ),
        activityLogs = listOf(
            HomeWorkoutLogUiModel(
                id = HOME_PREVIEW_ACTIVITY_FIRST_ID,
                timestamp = baseMillis,
                distanceKm = firstActivityDistance,
                repCount = 0,
                durationMinutes = HOME_PREVIEW_ACTIVITY_FIRST_DURATION_MINUTES,
                type = HomeWorkoutType.RUNNING
            ),
            HomeWorkoutLogUiModel(
                id = HOME_PREVIEW_ACTIVITY_SECOND_ID,
                timestamp = baseMillis - dayMillis,
                distanceKm = secondActivityCount.toDouble(),
                repCount = secondActivityCount,
                durationMinutes = HOME_PREVIEW_ACTIVITY_SECOND_DURATION_MINUTES,
                type = HomeWorkoutType.SQUAT
            )
        )
    )

    RunningGoalTrackerTheme {
        HomeScreen(
            uiState = uiState,
            onPeriodSelected = {},
            onNavigatePreviousPeriod = {},
            onNavigateNextPeriod = {},
            onDateSelected = {},
            onCalendarOpen = {},
            onCalendarDismiss = {},
            onCalendarPreviousMonth = {},
            onCalendarNextMonth = {},
            onGoalClick = {},
            onReminderClick = {},
            onViewAllActivitiesClick = {}
        )
    }
}
