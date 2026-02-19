package com.jeong.runninggoaltracker.feature.auth.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.jeong.runninggoaltracker.feature.auth.contract.ONBOARDING_AUTH_CHOICE_ANONYMOUS_TAG
import com.jeong.runninggoaltracker.feature.auth.contract.ONBOARDING_NICKNAME_INPUT_TAG
import com.jeong.runninggoaltracker.feature.auth.contract.ONBOARDING_PERMISSION_AGREE_TAG
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import org.junit.Rule
import org.junit.Test

class OnboardingFlowTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun permissionsToAuthChoiceToNicknameViaAnonymous() {
        val uiState = mutableStateOf(OnboardingUiState(step = OnboardingStep.Permissions))

        composeRule.setContent {
            RunningGoalTrackerTheme {
                OnboardingScreen(
                    uiState = uiState.value,
                    isPrivacyAccepted = false,
                    onRequestPermissions = {
                        uiState.value = uiState.value.copy(step = OnboardingStep.AuthChoice)
                    },
                    onOpenSettings = {},
                    onNicknameChanged = {},
                    onPrivacyAcceptedChange = {},
                    onContinue = {},
                    onContinueWithoutLogin = {
                        uiState.value = uiState.value.copy(step = OnboardingStep.Nickname)
                    },
                    onPrivacyPolicyClick = {},
                    onRetryInternet = {},
                    onDismissNoInternetDialog = {},
                    onComplete = {}
                )
            }
        }

        composeRule.onNodeWithTag(ONBOARDING_PERMISSION_AGREE_TAG).performClick()
        composeRule.onNodeWithTag(ONBOARDING_AUTH_CHOICE_ANONYMOUS_TAG).performClick()
        composeRule.onNodeWithTag(ONBOARDING_NICKNAME_INPUT_TAG).assertExists()
    }
}
