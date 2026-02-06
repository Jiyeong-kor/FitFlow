package com.jeong.runninggoaltracker.feature.mypage.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.jeong.runninggoaltracker.domain.model.AuthError
import com.jeong.runninggoaltracker.domain.model.RunningSummary
import com.jeong.runninggoaltracker.feature.mypage.R
import com.jeong.runninggoaltracker.feature.mypage.contract.MYPAGE_DISTANCE_SCALE_TENTHS
import com.jeong.runninggoaltracker.feature.mypage.contract.MYPAGE_PERCENT_BASE
import com.jeong.runninggoaltracker.feature.mypage.contract.MYPAGE_PREVIEW_PROGRESS_PERCENT
import com.jeong.runninggoaltracker.feature.mypage.contract.MYPAGE_PREVIEW_RECORD_COUNT
import com.jeong.runninggoaltracker.feature.mypage.contract.MYPAGE_PREVIEW_TOTAL_WEEK_KM_TENTHS
import com.jeong.runninggoaltracker.feature.mypage.contract.MYPAGE_PREVIEW_WEEKLY_GOAL_KM_TENTHS
import com.jeong.runninggoaltracker.feature.mypage.contract.MYPAGE_WEIGHT_ONE
import com.jeong.runninggoaltracker.feature.mypage.contract.MYPAGE_ZERO_INT
import com.jeong.runninggoaltracker.shared.designsystem.common.AppContentCard
import com.jeong.runninggoaltracker.shared.designsystem.common.AppProgressBar
import com.jeong.runninggoaltracker.shared.designsystem.common.AppSurfaceCard
import com.jeong.runninggoaltracker.shared.designsystem.common.AppSurfaceCardPadding
import com.jeong.runninggoaltracker.shared.designsystem.config.AppNumericTokens
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
import com.jeong.runninggoaltracker.shared.designsystem.extension.throttleClick
import com.jeong.runninggoaltracker.shared.designsystem.formatter.DistanceFormatter
import com.jeong.runninggoaltracker.shared.designsystem.formatter.PercentageFormatter
import com.jeong.runninggoaltracker.shared.designsystem.icon.AppIcons
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSpacingLg
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSpacingMd
import com.jeong.runninggoaltracker.shared.designsystem.theme.appSpacingSm
import java.text.NumberFormat

@Composable
fun MyPageScreen(
    uiState: MyPageUiState,
    deleteAccountState: DeleteAccountUiState,
    onNavigateToGoal: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onActivityToggle: (Boolean) -> Unit,
    onDeleteAccountClick: () -> Unit,
    onDeleteAccountConfirm: () -> Unit,
    onDeleteAccountDialogDismiss: () -> Unit,
    onDeleteAccountStateConsumed: () -> Unit
) {
    MyPageContent(
        uiState = uiState,
        onNavigateToGoal = onNavigateToGoal,
        onNavigateToReminder = onNavigateToReminder,
        onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy,
        deleteAccountState = deleteAccountState,
        onActivityToggle = onActivityToggle,
        onDeleteAccountClick = onDeleteAccountClick,
        onDeleteAccountConfirm = onDeleteAccountConfirm,
        onDeleteAccountDialogDismiss = onDeleteAccountDialogDismiss,
        onDeleteAccountStateConsumed = onDeleteAccountStateConsumed
    )
}

@Composable
private fun MyPageContent(
    uiState: MyPageUiState,
    onNavigateToGoal: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onActivityToggle: (Boolean) -> Unit,
    deleteAccountState: DeleteAccountUiState,
    onDeleteAccountClick: () -> Unit,
    onDeleteAccountConfirm: () -> Unit,
    onDeleteAccountDialogDismiss: () -> Unit,
    onDeleteAccountStateConsumed: () -> Unit
) {
    val dimensions = LocalAppDimensions.current
    val openDeleteDialog = rememberThrottleClick(onClick = onDeleteAccountClick)
    val closeDeleteDialog = rememberThrottleClick(onClick = onDeleteAccountDialogDismiss)
    val confirmDeleteDialog = rememberThrottleClick(
        onClick = {
            onDeleteAccountConfirm()
        }
    )

    if (uiState.isDeleteDialogVisible) {
        AlertDialog(
            onDismissRequest = closeDeleteDialog,
            title = { Text(text = stringResource(id = R.string.mypage_delete_account_confirm_title)) },
            text = { Text(text = stringResource(id = R.string.mypage_delete_account_confirm_desc)) },
            confirmButton = {
                TextButton(onClick = confirmDeleteDialog) {
                    Text(text = stringResource(id = R.string.mypage_delete_account_confirm_button))
                }
            },
            dismissButton = {
                TextButton(onClick = closeDeleteDialog) {
                    Text(text = stringResource(id = R.string.mypage_delete_account_cancel_button))
                }
            }
        )
    }

    when (deleteAccountState) {
        DeleteAccountUiState.Success -> {
            val closeSuccessDialog = rememberThrottleClick(
                onClick = {
                    onDeleteAccountStateConsumed()
                }
            )
            AlertDialog(
                onDismissRequest = closeSuccessDialog,
                title = { Text(text = stringResource(id = R.string.mypage_delete_account_success_title)) },
                text = { Text(text = stringResource(id = R.string.mypage_delete_account_success_desc)) },
                confirmButton = {
                    TextButton(onClick = closeSuccessDialog) {
                        Text(text = stringResource(id = R.string.mypage_delete_account_confirm_button))
                    }
                }
            )
        }

        is DeleteAccountUiState.Failure -> {
            val closeErrorDialog = rememberThrottleClick(
                onClick = {
                    onDeleteAccountStateConsumed()
                }
            )
            AlertDialog(
                onDismissRequest = closeErrorDialog,
                title = { Text(text = stringResource(id = R.string.mypage_delete_account_error_title)) },
                text = {
                    Text(
                        text = stringResource(
                            id = deleteAccountErrorMessageRes(deleteAccountState.error)
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = closeErrorDialog) {
                        Text(text = stringResource(id = R.string.mypage_delete_account_confirm_button))
                    }
                }
            )
        }

        DeleteAccountUiState.Idle,
        DeleteAccountUiState.Loading -> Unit
    }

    Scaffold(
        topBar = { }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
                .padding(dimensions.myPageScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensions.myPageSectionSpacing
            )
        ) {
            ProfileSection(uiState.userNickname, uiState.userLevel, uiState.isAnonymous)

            if (uiState.isAnonymous) {
                AppSurfaceCard(padding = AppSurfaceCardPadding.Large) {
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingMd())) {
                        val upgradeAccountClick = rememberThrottleClick(onClick = {})
                        Text(
                            text = stringResource(id = R.string.mypage_anonymous_info_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(id = R.string.mypage_anonymous_info_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(onClick = upgradeAccountClick) {
                            Text(text = stringResource(id = R.string.mypage_btn_upgrade_account))
                        }
                    }
                }
            }

            SummaryStats(uiState)

            GoalProgressCard(uiState, onNavigateToGoal)

            SettingsList(
                uiState = uiState,
                onNavigateToReminder = onNavigateToReminder,
                onNavigateToGoal = onNavigateToGoal,
                onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy,
                onActivityToggle = onActivityToggle,
                onDeleteAccount = openDeleteDialog
            )
        }
    }
}

@Composable
private fun ProfileSection(name: String?, level: String?, isAnonymous: Boolean) {
    val dimensions = LocalAppDimensions.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val displayName = if (name.isNullOrBlank()) {
            stringResource(id = R.string.mypage_default_nickname)
        } else {
            name
        }
        val displayLevel = if (level.isNullOrBlank()) {
            stringResource(id = R.string.mypage_default_level)
        } else {
            level
        }
        Surface(
            modifier = Modifier.size(dimensions.myPageProfileSize),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                painter = AppIcons.person(),
                contentDescription = null,
                modifier = Modifier.padding(dimensions.myPageProfileIconPadding),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(appSpacingSm())
        ) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (isAnonymous) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Text(
                        text = stringResource(id = R.string.mypage_guest_mode_status),
                        modifier = Modifier.padding(
                            horizontal = appSpacingSm(),
                            vertical = appSpacingSm()
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = displayLevel,
                modifier = Modifier.padding(
                    horizontal = dimensions.myPageLevelBadgeHorizontalPadding,
                    vertical = dimensions.myPageLevelBadgeVerticalPadding
                ),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun SummaryStats(uiState: MyPageUiState) {
    val dimensions = LocalAppDimensions.current
    val zeroInt = MYPAGE_ZERO_INT
    val zeroDouble = zeroInt.toDouble()
    val zeroFloat = zeroInt.toFloat()
    val weightOne = MYPAGE_WEIGHT_ONE
    val locale = LocalConfiguration.current.locales[0]
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            dimensions.myPageSummarySpacing
        )
    ) {
        LocalContext.current
        val distanceFormatter = remember(locale) {
            DistanceFormatter(
                localeProvider = { locale },
                numberFormatFactory = NumberFormat::getNumberInstance
            )
        }
        val percentageFormatter = remember(locale) {
            PercentageFormatter(
                localeProvider = { locale },
                numberFormatFactory = NumberFormat::getNumberInstance
            )
        }
        val distanceFractionDigits = AppNumericTokens.DISTANCE_FRACTION_DIGITS
        val percentageFractionDigits = AppNumericTokens.PERCENTAGE_FRACTION_DIGITS
        val percentageScale = AppNumericTokens.PERCENTAGE_SCALE
        val distanceText =
            distanceFormatter.formatDistanceKm(
                distanceKm = uiState.summary?.totalThisWeekKm ?: zeroDouble,
                fractionDigits = distanceFractionDigits
            )
        val progressText =
            percentageFormatter.formatProgress(
                progress = uiState.summary?.progress ?: zeroFloat,
                fractionDigits = percentageFractionDigits,
                percentScale = percentageScale
            )
        StatItem(
            modifier = Modifier.weight(weightOne),
            label = stringResource(id = R.string.mypage_summary_distance_label),
            value = stringResource(id = R.string.mypage_summary_distance_value, distanceText)
        )
        StatItem(
            modifier = Modifier.weight(weightOne),
            label = stringResource(id = R.string.mypage_summary_count_label),
            value = stringResource(
                id = R.string.mypage_summary_count_value,
                uiState.summary?.recordCountThisWeek ?: zeroInt
            )
        )
        StatItem(
            modifier = Modifier.weight(weightOne),
            label = stringResource(id = R.string.mypage_summary_progress_label),
            value = stringResource(id = R.string.mypage_summary_progress_value, progressText)
        )
    }
}

@Composable
private fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    val dimensions = LocalAppDimensions.current
    AppContentCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(dimensions.myPageStatItemPadding)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GoalProgressCard(uiState: MyPageUiState, onClick: () -> Unit) {
    val throttledOnClick = rememberThrottleClick(onClick = onClick)
    val dimensions = LocalAppDimensions.current
    val zeroFloat = MYPAGE_ZERO_INT.toFloat()
    val weightOne = MYPAGE_WEIGHT_ONE
    AppContentCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(dimensions.myPageGoalCardPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.mypage_goal_progress_title),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(weightOne))
                TextButton(onClick = throttledOnClick) {
                    Text(text = stringResource(id = R.string.mypage_goal_progress_detail))
                }
            }
            AppProgressBar(
                progress = uiState.summary?.progress ?: zeroFloat,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SettingsList(
    uiState: MyPageUiState,
    onNavigateToReminder: () -> Unit,
    onNavigateToGoal: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onActivityToggle: (Boolean) -> Unit,
    onDeleteAccount: () -> Unit
) {
    val dimensions = LocalAppDimensions.current
    AppContentCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            val activityToggleLabel = stringResource(id = R.string.mypage_setting_activity_toggle)
            SettingItem(
                icon = AppIcons::notifications,
                title = stringResource(id = R.string.mypage_setting_notification_title),
                subTitle = stringResource(id = R.string.mypage_setting_notification_desc),
                onClick = onNavigateToReminder
            )
            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = dimensions.myPageDividerHorizontalPadding
                ),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            SettingItem(
                icon = AppIcons::edit,
                title = stringResource(id = R.string.mypage_setting_goal_title),
                subTitle = stringResource(id = R.string.mypage_setting_goal_desc),
                onClick = onNavigateToGoal
            )
            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = dimensions.myPageDividerHorizontalPadding
                ),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            SettingItem(
                icon = AppIcons::description,
                title = stringResource(id = R.string.mypage_setting_privacy_policy_title),
                subTitle = stringResource(id = R.string.mypage_setting_privacy_policy_desc),
                onClick = onNavigateToPrivacyPolicy
            )
            HorizontalDivider(
                modifier = Modifier.padding(
                    horizontal = dimensions.myPageDividerHorizontalPadding
                ),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            Row(
                modifier = Modifier.padding(dimensions.myPageSettingRowPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(AppIcons.refresh(), null, tint = MaterialTheme.colorScheme.primary)
                Column(
                    modifier = Modifier
                        .padding(start = dimensions.myPageSettingTitleSpacing)
                        .weight(MYPAGE_WEIGHT_ONE)
                ) {
                    Text(
                        text = stringResource(id = R.string.mypage_setting_activity_title),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(id = R.string.mypage_setting_activity_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Switch(
                    checked = uiState.isActivityRecognitionEnabled,
                    onCheckedChange = { enabled ->
                        onActivityToggle(enabled)
                    },
                    modifier = Modifier.semantics {
                        contentDescription = activityToggleLabel
                    }
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = appSpacingLg()),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            SettingItem(
                icon = AppIcons::delete,
                title = stringResource(id = R.string.mypage_setting_delete_account_title),
                subTitle = stringResource(id = R.string.mypage_setting_delete_account_desc),
                onClick = onDeleteAccount
            )
        }
    }
}

@Composable
private fun SettingItem(
    icon: @Composable () -> Painter,
    title: String,
    subTitle: String,
    onClick: () -> Unit
) {
    val dimensions = LocalAppDimensions.current
    val weightOne = MYPAGE_WEIGHT_ONE
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { role = Role.Button }
            .throttleClick(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(dimensions.myPageSettingRowPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon(), null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.padding(start = dimensions.myPageSettingTitleSpacing)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    subTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(Modifier.weight(weightOne))
            Icon(
                AppIcons.keyboardArrowRight(),
                null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() =
    RunningGoalTrackerTheme {
        val previewNickname = stringResource(id = R.string.mypage_default_nickname)
        val previewLevel = stringResource(id = R.string.mypage_default_level)
        val distanceScale = MYPAGE_DISTANCE_SCALE_TENTHS
        val progressBase = MYPAGE_PERCENT_BASE
        val weeklyGoalKm =
            MYPAGE_PREVIEW_WEEKLY_GOAL_KM_TENTHS.toDouble() / distanceScale
        val totalThisWeekKm =
            MYPAGE_PREVIEW_TOTAL_WEEK_KM_TENTHS.toDouble() / distanceScale
        val progress =
            MYPAGE_PREVIEW_PROGRESS_PERCENT.toFloat() / progressBase
        MyPageContent(
            uiState = MyPageUiState(
                isLoading = false,
                summary = RunningSummary(
                    weeklyGoalKm = weeklyGoalKm,
                    totalThisWeekKm = totalThisWeekKm,
                    recordCountThisWeek = MYPAGE_PREVIEW_RECORD_COUNT,
                    progress = progress
                ),
                userNickname = previewNickname,
                userLevel = previewLevel,
                isActivityRecognitionEnabled = true,
                isAnonymous = true
            ),
            onNavigateToGoal = {},
            onNavigateToReminder = {},
            onNavigateToPrivacyPolicy = {},
            onActivityToggle = {},
            onDeleteAccountClick = {},
            onDeleteAccountConfirm = {},
            onDeleteAccountDialogDismiss = {},
            deleteAccountState = DeleteAccountUiState.Idle,
            onDeleteAccountStateConsumed = {}
        )
    }

private fun deleteAccountErrorMessageRes(error: AuthError): Int =
    when (error) {
        AuthError.NetworkError ->
            R.string.mypage_delete_account_error_desc_network

        AuthError.PermissionDenied ->
            R.string.mypage_delete_account_error_desc_permission

        AuthError.Unknown ->
            R.string.mypage_delete_account_error_desc_unknown

        AuthError.NicknameTaken ->
            R.string.mypage_delete_account_error_desc_unknown
    }
