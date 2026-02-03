package com.jeong.runninggoaltracker.feature.auth.presentation

import androidx.annotation.StringRes
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.jeong.runninggoaltracker.feature.auth.R
import com.jeong.runninggoaltracker.shared.designsystem.common.AppContentCard
import com.jeong.runninggoaltracker.shared.designsystem.common.AppSurfaceCard
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppShapes
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    isPrivacyAccepted: Boolean,
    onRequestPermissions: () -> Unit,
    onOpenSettings: () -> Unit,
    onNicknameChanged: (String) -> Unit,
    onPrivacyAcceptedChange: (Boolean) -> Unit,
    onContinue: () -> Unit,
    onKakaoLogin: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onRetryInternet: () -> Unit,
    onDismissNoInternetDialog: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val openSettingsThrottled = rememberThrottleClick {
        onOpenSettings()
    }

    when (uiState.step) {
        OnboardingStep.Permissions -> PermissionsScreen(
            modifier = modifier,
            permissionErrorResId = uiState.permissionErrorResId,
            showSettingsAction = uiState.isPermissionPermanentlyDenied,
            onAgree = onRequestPermissions,
            onOpenSettings = openSettingsThrottled
        )

        OnboardingStep.Nickname -> NicknameScreen(
            modifier = modifier,
            uiState = uiState,
            isPrivacyAccepted = isPrivacyAccepted,
            onNicknameChanged = onNicknameChanged,
            onPrivacyAcceptedChange = onPrivacyAcceptedChange,
            onContinue = onContinue,
            onKakaoLogin = onKakaoLogin,
            onPrivacyPolicyClick = onPrivacyPolicyClick
        )

        OnboardingStep.Success -> SuccessScreen(
            modifier = modifier,
            onContinue = onComplete
        )
    }

    if (uiState.showNoInternetDialog) {
        NoInternetDialog(
            onRetry = onRetryInternet,
            onDismiss = onDismissNoInternetDialog
        )
    }
}

@Composable
private fun PermissionsScreen(
    @StringRes permissionErrorResId: Int?,
    showSettingsAction: Boolean,
    onAgree: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalAppDimensions.current
    val onAgreeThrottled = rememberThrottleClick(onClick = onAgree)
    val onOpenSettingsThrottled = rememberThrottleClick(onClick = onOpenSettings)

    val permissions = listOf(
        PermissionItem(
            icon = Icons.AutoMirrored.Outlined.DirectionsRun,
            titleResId = R.string.permission_activity_title,
            descriptionResId = R.string.permission_activity_description,
            isEssential = true
        ),
        PermissionItem(
            icon = Icons.Outlined.LocationOn,
            titleResId = R.string.permission_location_title,
            descriptionResId = R.string.permission_location_description,
            isEssential = true
        ),
        PermissionItem(
            icon = Icons.Outlined.PhotoCamera,
            titleResId = R.string.permission_camera_title,
            descriptionResId = R.string.permission_camera_description,
            isEssential = true
        ),
        PermissionItem(
            icon = Icons.Outlined.Notifications,
            titleResId = R.string.permission_notification_title,
            descriptionResId = R.string.permission_notification_description,
            isEssential = true
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimensions.spacingXl, vertical = dimensions.spacing2xl)
    ) {
        HeaderIcon()
        Spacer(modifier = Modifier.height(dimensions.spacingMd))
        Text(
            text = stringResource(id = R.string.permission_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(dimensions.spacingSm))
        Text(
            text = stringResource(id = R.string.permission_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(dimensions.spacingLg))
        permissions.forEach { permission ->
            AppContentCard {
                PermissionRow(
                    item = permission,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(dimensions.spacingMd))
        }

        if (permissionErrorResId != null) {
            Text(
                text = stringResource(id = permissionErrorResId),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(dimensions.spacingMd))
        }

        Spacer(modifier = Modifier.height(dimensions.spacingLg))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onAgreeThrottled,
            contentPadding = PaddingValues(vertical = dimensions.spacingLg)
        ) {
            Text(
                text = stringResource(id = R.string.permission_agree),
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (showSettingsAction) {
            Spacer(modifier = Modifier.height(dimensions.spacingMd))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenSettingsThrottled,
                contentPadding = PaddingValues(vertical = dimensions.spacingLg)
            ) {
                Text(
                    text = stringResource(id = R.string.permission_open_settings),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun PermissionRow(
    item: PermissionItem,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalAppDimensions.current
    val appShapes = LocalAppShapes.current
    val weightOne = integerResource(id = R.integer.permission_weight_one).toFloat()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(weightOne)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSm)
            ) {
                Text(
                    text = stringResource(id = item.titleResId),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (item.isEssential) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = appShapes.roundedSm
                    ) {
                        Text(
                            text = stringResource(id = R.string.permission_essential),
                            modifier = Modifier.padding(
                                horizontal = dimensions.onboardingTagPaddingHorizontal,
                                vertical = dimensions.onboardingTagPaddingVertical
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(dimensions.spacingSm))
            Text(
                text = stringResource(id = item.descriptionResId),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SuccessScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalAppDimensions.current
    val weightOne = integerResource(id = R.integer.permission_weight_one).toFloat()
    val onContinueThrottled = rememberThrottleClick(onClick = onContinue)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = dimensions.spacingXl, vertical = dimensions.spacing2xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(weightOne))
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.DirectionsRun,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(dimensions.onboardingIconSize)
        )
        Spacer(modifier = Modifier.height(dimensions.spacingLg))
        Text(
            text = stringResource(id = R.string.success_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(dimensions.spacingLg))
        Text(
            text = stringResource(id = R.string.success_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(weightOne))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onContinueThrottled,
            contentPadding = PaddingValues(vertical = dimensions.spacingLg)
        ) {
            Text(
                text = stringResource(id = R.string.success_continue),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private data class PermissionItem(
    val icon: ImageVector,
    @field:StringRes val titleResId: Int,
    @field:StringRes val descriptionResId: Int,
    val isEssential: Boolean
)

@Composable
private fun HeaderIcon() {
    val dimensions = LocalAppDimensions.current
    val appShapes = LocalAppShapes.current

    Card(
        modifier = Modifier.size(dimensions.onboardingIconContainerSize),
        shape = appShapes.rounded2xl,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.onboardingCardElevation)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.DirectionsRun,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensions.onboardingIconSize)
            )
        }
    }
}

@Composable
private fun NoInternetDialog(
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val dimensions = LocalAppDimensions.current
    val onRetryThrottled = rememberThrottleClick(onClick = onRetry)

    Dialog(onDismissRequest = onDismiss) {
        AppSurfaceCard(
            modifier = Modifier.padding(horizontal = dimensions.spacingXl)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacingMd)) {
                Text(
                    text = stringResource(id = R.string.no_internet_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.no_internet_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(dimensions.spacingSm))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRetryThrottled,
                    contentPadding = PaddingValues(vertical = dimensions.spacingLg)
                ) {
                    Text(
                        text = stringResource(id = R.string.no_internet_retry),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PermissionsScreenPreview() =
    RunningGoalTrackerTheme {
        PermissionsScreen(
            permissionErrorResId = null,
            showSettingsAction = false,
            onAgree = {},
            onOpenSettings = {}
        )
    }

@Preview(showBackground = true)
@Composable
private fun PermissionRowPreview() =
    RunningGoalTrackerTheme {
        PermissionRow(
            item = PermissionItem(
                icon = Icons.Outlined.LocationOn,
                titleResId = R.string.permission_location_title,
                descriptionResId = R.string.permission_location_description,
                isEssential = true
            )
        )
    }

@Preview(showBackground = true)
@Composable
private fun SuccessScreenPreview() =
    RunningGoalTrackerTheme {
        SuccessScreen(onContinue = {})
    }

@Preview(showBackground = true)
@Composable
private fun HeaderIconPreview() =
    RunningGoalTrackerTheme {
        HeaderIcon()
    }

@Preview(showBackground = true)
@Composable
private fun NoInternetDialogPreview() =
    RunningGoalTrackerTheme {
        NoInternetDialog(
            onRetry = {},
            onDismiss = {}
        )
    }
