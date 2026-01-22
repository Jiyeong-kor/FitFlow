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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jeong.runninggoaltracker.feature.home.R
import com.jeong.runninggoaltracker.shared.designsystem.common.AppSurfaceCard
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeRoute(
    onNavigateToRecord: () -> Unit,
    onNavigateToGoal: () -> Unit,
    onNavigateToReminder: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeUiEffect.NavigateToRecord -> onNavigateToRecord()
                HomeUiEffect.NavigateToGoal -> onNavigateToGoal()
                HomeUiEffect.NavigateToReminder -> onNavigateToReminder()
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        onPeriodSelected = viewModel::onPeriodSelected,
        onNavigatePreviousPeriod = viewModel::onNavigatePreviousPeriod,
        onNavigateNextPeriod = viewModel::onNavigateNextPeriod,
        onDateSelected = viewModel::onDateSelected,
        onRecordClick = viewModel::onRecordClick,
        onGoalClick = viewModel::onGoalClick,
        onReminderClick = viewModel::onReminderClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onPeriodSelected: (PeriodState) -> Unit,
    onNavigatePreviousPeriod: () -> Unit,
    onNavigateNextPeriod: () -> Unit,
    onDateSelected: (Long) -> Unit,
    onRecordClick: () -> Unit,
    onGoalClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    val accentColor = appAccentColor()
    val backgroundColor = appBackgroundColor()
    val surfaceColor = appSurfaceColor()
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val horizontalPadding = appSpacingLg()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isCalendarVisible by rememberSaveable { mutableStateOf(false) }
    val onRecordClickThrottled = rememberThrottleClick(onClick = onRecordClick)
    val onGoalClickThrottled = rememberThrottleClick(onClick = onGoalClick)
    val onReminderClickThrottled = rememberThrottleClick(onClick = onReminderClick)

    if (isCalendarVisible) {
        CalendarBottomSheet(
            selectedDateMillis = uiState.selectedDateState.dateMillis,
            onDateSelected = {
                onDateSelected(it)
                isCalendarVisible = false
            },
            onDismiss = { isCalendarVisible = false },
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
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { isCalendarVisible = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = stringResource(R.string.home_action_select_date),
                            tint = textPrimary
                        )
                    }
                }
                DateNavigator(
                    periodState = uiState.periodState,
                    selectedDateMillis = uiState.selectedDateState.dateMillis,
                    onNavigatePreviousPeriod = onNavigatePreviousPeriod,
                    onNavigateNextPeriod = onNavigateNextPeriod
                )
            }
        }

        item {
            AnimatedContent(targetState = uiState.summary, label = "summary") { summary ->
                SummaryCard(
                    summary = summary,
                    accentColor = accentColor,
                    textPrimary = textPrimary,
                    textMuted = textMuted,
                    surfaceColor = surfaceColor
                )
            }
        }

        item {
            SectionHeader(
                titleResId = R.string.home_section_activity_log,
                onViewAllClick = onRecordClickThrottled
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
            items(uiState.activityLogs, key = { it.id }) { activity ->
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
                        .padding(horizontal = appSpacingSm(), vertical = appSpacingSm())
                )
            }
        }

        item {
            AppSurfaceCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGoalClickThrottled)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(appSpacingSm())) {
                    Text(
                        text = stringResource(R.string.home_goal_summary_title),
                        color = textMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = stringResource(R.string.home_goal_summary_description),
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
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) }
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
    onNavigatePreviousPeriod: () -> Unit,
    onNavigateNextPeriod: () -> Unit
) {
    val label = periodLabel(periodState, selectedDateMillis)
    val textPrimary = appTextPrimaryColor()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigatePreviousPeriod) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        IconButton(onClick = onNavigateNextPeriod) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.home_action_next_period),
                tint = textPrimary
            )
        }
    }
}

@Composable
private fun SummaryCard(
    summary: HomeSummaryUiState,
    accentColor: androidx.compose.ui.graphics.Color,
    textPrimary: androidx.compose.ui.graphics.Color,
    textMuted: androidx.compose.ui.graphics.Color,
    surfaceColor: androidx.compose.ui.graphics.Color
) {
    AppSurfaceCard(containerColor = surfaceColor) {
        Column(verticalArrangement = Arrangement.spacedBy(appSpacingMd())) {
            Text(
                text = stringResource(R.string.home_summary_title),
                color = textMuted,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.home_distance_value_format, summary.totalDistanceKm),
                color = textPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.home_distance_unit_label),
                color = textMuted,
                style = MaterialTheme.typography.labelSmall
            )

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
                    labelResId = R.string.home_metric_avg_pace,
                    value = paceLabel(summary.averagePace),
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
    Column(
        modifier = Modifier
            .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(appSpacingSm()))
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
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
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
    val icon = when (activity.type) {
        HomeWorkoutType.RUNNING -> Icons.AutoMirrored.Filled.DirectionsRun
        HomeWorkoutType.SQUAT -> Icons.Default.FitnessCenter
    }
    val dateLabel = activityDateLabel(activity.timestamp)
    val distanceLabel = if (activity.distanceKm > 0) {
        stringResource(R.string.home_distance_km_format, activity.distanceKm)
    } else {
        stringResource(R.string.home_distance_placeholder)
    }
    val durationLabel = if (activity.durationMinutes > 0) {
        stringResource(R.string.home_duration_min_format, activity.durationMinutes)
    } else {
        stringResource(R.string.home_duration_placeholder)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceColor, RoundedCornerShape(appSpacingMd()))
            .padding(appSpacingMd()),
        horizontalArrangement = Arrangement.spacedBy(appSpacingMd()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(surfaceColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textMuted
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    R.string.home_activity_title_format,
                    distanceLabel,
                    stringResource(activity.typeLabelResId)
                ),
                color = textPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(
                    R.string.home_activity_subtitle_format,
                    dateLabel,
                    durationLabel
                ),
                color = textMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SectionHeader(
    @StringRes titleResId: Int,
    onViewAllClick: () -> Unit
) {
    val accentColor = appAccentColor()
    val textPrimary = appTextPrimaryColor()
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
                .padding(horizontal = appSpacingSm(), vertical = appSpacingSm())
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun CalendarBottomSheet(
    selectedDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val accentColor = appAccentColor()
    val onAccentColor = appOnAccentColor()
    val initialMonth = remember(selectedDateMillis) { yearMonthFromMillis(selectedDateMillis) }
    var visibleYear by rememberSaveable { mutableIntStateOf(initialMonth.year) }
    var visibleMonth by rememberSaveable { mutableIntStateOf(initialMonth.month) }
    val visibleState = YearMonthState(year = visibleYear, month = visibleMonth)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
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
                    onClick = {
                        val shifted = visibleState.copy(monthOffset = -1)
                        visibleYear = shifted.year
                        visibleMonth = shifted.month
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.home_action_previous_month),
                        tint = textPrimary
                    )
                }
                Text(
                    text = visibleState.label,
                    color = textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = {
                        val shifted = visibleState.copy(monthOffset = 1)
                        visibleYear = shifted.year
                        visibleMonth = shifted.month
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            val calendarDays = visibleState.buildCalendarDays()
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(appSpacingSm()),
                verticalArrangement = Arrangement.spacedBy(appSpacingSm()),
                modifier = Modifier.height(280.dp),
                userScrollEnabled = false
            ) {
                items(calendarDays) { day ->
                    if (day == null) {
                        Spacer(modifier = Modifier.size(32.dp))
                    } else {
                        val isSelected = day.isSameDay(selectedDateMillis)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = if (isSelected) accentColor else appSurfaceColor(),
                                    shape = CircleShape
                                )
                                .clickable { onDateSelected(day.timestampMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.dayOfMonth.toString(),
                                color = if (isSelected) onAccentColor else textPrimary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun periodLabel(periodState: PeriodState, selectedDateMillis: Long): String {
    val locale = Locale.getDefault()
    val dayFormatter = remember { SimpleDateFormat("MMM d, yyyy", locale) }
    val monthFormatter = remember { SimpleDateFormat("MMMM yyyy", locale) }
    val rangeFormatter = remember { SimpleDateFormat("MMM d", locale) }
    return when (periodState) {
        PeriodState.DAILY -> stringResource(
            R.string.home_period_daily_format,
            dayFormatter.format(selectedDateMillis)
        )

        PeriodState.WEEKLY -> {
            val (start, end) = weekRange(selectedDateMillis)
            stringResource(
                R.string.home_period_weekly_range_format,
                rangeFormatter.format(start),
                rangeFormatter.format(end)
            )
        }

        PeriodState.MONTHLY -> stringResource(
            R.string.home_period_monthly_format,
            monthFormatter.format(selectedDateMillis)
        )
    }
}

@Composable
private fun paceLabel(pace: HomePaceUiState): String {
    return if (pace.isAvailable) {
        stringResource(R.string.home_pace_format, pace.minutes, pace.seconds)
    } else {
        stringResource(R.string.home_pace_placeholder)
    }
}

@Composable
private fun activityDateLabel(timestampMillis: Long): String {
    val locale = Locale.getDefault()
    val formatter = remember { SimpleDateFormat("MMM d", locale) }
    return formatter.format(timestampMillis)
}

private fun weekRange(selectedDateMillis: Long): Pair<Long, Long> {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = selectedDateMillis
        firstDayOfWeek = Calendar.SUNDAY
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val start = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_YEAR, 6)
    val end = calendar.timeInMillis
    return start to end
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

private data class YearMonthState(
    val year: Int,
    val month: Int
) {
    val label: String
        get() {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
            }
            return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.timeInMillis)
        }

    fun buildCalendarDays(): List<CalendarDay?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val offset = (firstDayOfWeek - Calendar.SUNDAY).let { if (it < 0) it + 7 else it }
        val totalCells = offset + daysInMonth
        return buildList {
            repeat(offset) { add(null) }
            for (day in 1..daysInMonth) {
                calendar.set(Calendar.DAY_OF_MONTH, day)
                add(
                    CalendarDay(
                        dayOfMonth = day,
                        timestampMillis = calendar.timeInMillis
                    )
                )
            }
            while (size < totalCells) {
                add(null)
            }
        }
    }

    fun copy(monthOffset: Int): YearMonthState {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            add(Calendar.MONTH, monthOffset)
        }
        return YearMonthState(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH)
        )
    }
}

private data class CalendarDay(
    val dayOfMonth: Int,
    val timestampMillis: Long
) {
    fun isSameDay(otherMillis: Long): Boolean {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        val other = Calendar.getInstance().apply { timeInMillis = otherMillis }
        return calendar.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
    }
}

private fun yearMonthFromMillis(dateMillis: Long): YearMonthState {
    val calendar = Calendar.getInstance().apply { timeInMillis = dateMillis }
    return YearMonthState(
        year = calendar.get(Calendar.YEAR),
        month = calendar.get(Calendar.MONTH)
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val uiState = HomeUiState(
        periodState = PeriodState.WEEKLY,
        summary = HomeSummaryUiState(
            totalDistanceKm = 12.5,
            totalCalories = 740,
            totalDurationMinutes = 85,
            averagePace = HomePaceUiState(minutes = 6, seconds = 48, isAvailable = true)
        ),
        activityLogs = listOf(
            HomeWorkoutLogUiModel(
                id = 1,
                timestamp = System.currentTimeMillis(),
                distanceKm = 5.2,
                durationMinutes = 32,
                type = HomeWorkoutType.RUNNING,
                typeLabelResId = R.string.activity_running
            ),
            HomeWorkoutLogUiModel(
                id = 2,
                timestamp = System.currentTimeMillis() - 86_400_000L,
                distanceKm = 0.0,
                durationMinutes = 20,
                type = HomeWorkoutType.SQUAT,
                typeLabelResId = R.string.home_activity_squat
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
            onRecordClick = {},
            onGoalClick = {},
            onReminderClick = {}
        )
    }
}
