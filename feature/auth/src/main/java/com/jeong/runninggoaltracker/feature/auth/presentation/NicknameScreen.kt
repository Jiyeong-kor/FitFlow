package com.jeong.runninggoaltracker.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.jeong.runninggoaltracker.feature.auth.R
import com.jeong.runninggoaltracker.feature.auth.contract.ONBOARDING_NICKNAME_INPUT_TAG
import com.jeong.runninggoaltracker.shared.designsystem.common.AppSurfaceCard
import com.jeong.runninggoaltracker.shared.designsystem.common.AppSurfaceCardPadding
import com.jeong.runninggoaltracker.shared.designsystem.extension.rememberThrottleClick
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppShapes
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme

@Composable
fun NicknameScreen(
    uiState: OnboardingUiState,
    isPrivacyAccepted: Boolean,
    onNicknameChanged: (String) -> Unit,
    onPrivacyAcceptedChange: (Boolean) -> Unit,
    onContinue: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalAppDimensions.current
    val appShapes = LocalAppShapes.current
    val privacyPolicyLabel = stringResource(id = R.string.privacy_policy_agreement_link)
    val privacyPolicySuffix = stringResource(id = R.string.privacy_policy_agreement_suffix)
    val privacyPolicyAccessibilityLabel =
        stringResource(id = R.string.privacy_policy_agreement_full)

    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimensions.spacingXl, vertical = dimensions.spacing2xl),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingLg)
    ) {
        Text(
            text = stringResource(id = R.string.nickname_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(id = R.string.nickname_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AppSurfaceCard(padding = AppSurfaceCardPadding.Wide) {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingSm)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ONBOARDING_NICKNAME_INPUT_TAG),
                    value = uiState.nickname,
                    onValueChange = onNicknameChanged,
                    enabled = !uiState.isLoading,
                    isError = uiState.nicknameValidationMessage != null || uiState.nicknameHintError,
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Person, contentDescription = null)
                    },
                    label = { Text(text = stringResource(id = R.string.nickname_label)) },
                    singleLine = true,
                    shape = appShapes.rounded2xl
                )
                Row(modifier = Modifier.padding(top = dimensions.spacingSm)) {
                    Text(
                        text = stringResource(id = R.string.anonymous_nickname_caption),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(id = R.string.nickname_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (uiState.nicknameHintError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
        if (uiState.nicknameValidationMessage != null) {
            Text(
                text = stringResource(id = uiState.nicknameValidationMessage),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        if (uiState.nicknameAvailabilityMessageResId != null) {
            Text(
                text = stringResource(id = uiState.nicknameAvailabilityMessageResId),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (uiState.errorMessageResId != null) {
            Text(
                text = stringResource(id = uiState.errorMessageResId),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingXs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isPrivacyAccepted,
                onCheckedChange = onPrivacyAcceptedChange,
                enabled = !uiState.isLoading,
                modifier = Modifier.semantics {
                    contentDescription = privacyPolicyAccessibilityLabel
                }
            )
            Row(
                modifier = Modifier.semantics(mergeDescendants = true) {
                    contentDescription = privacyPolicyAccessibilityLabel
                }
            ) {
                Text(
                    text = privacyPolicyLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.clickable(
                        enabled = !uiState.isLoading,
                        role = Role.Button,
                        onClick = onPrivacyPolicyClick
                    )
                )
                Text(
                    text = privacyPolicySuffix,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
        val onContinueThrottled = rememberThrottleClick(onClick = onContinue)
        Spacer(modifier = Modifier.height(dimensions.spacingSm))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onContinueThrottled,
            enabled = uiState.isNicknameValid && isPrivacyAccepted && !uiState.isLoading,
            contentPadding = PaddingValues(vertical = dimensions.spacingLg)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimensions.spacingLg),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = dimensions.spacingXs
                )
            } else {
                Text(
                    text = stringResource(id = R.string.nickname_continue),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NicknameScreenPreview() = RunningGoalTrackerTheme {
    NicknameScreen(
        uiState = previewUiState(),
        isPrivacyAccepted = false,
        onNicknameChanged = {},
        onPrivacyAcceptedChange = {},
        onContinue = {},
        onPrivacyPolicyClick = {}
    )
}

@Composable
private fun previewUiState(): OnboardingUiState =
    OnboardingUiState(nickname = stringResource(id = R.string.nickname_preview_value))
