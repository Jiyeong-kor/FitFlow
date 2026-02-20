package com.jeong.fitflow.feature.mypage.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MyPageRoute(
    viewModel: MyPageViewModel,
    onNavigateToGoal: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteAccountState by viewModel.deleteAccountState.collectAsStateWithLifecycle()

    MyPageScreen(
        uiState = uiState,
        deleteAccountState = deleteAccountState,
        onNavigateToGoal = onNavigateToGoal,
        onNavigateToReminder = onNavigateToReminder,
        onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy,
        onActivityToggle = viewModel::toggleActivityRecognition,
        onDeleteAccountClick = viewModel::showDeleteAccountDialog,
        onDeleteAccountConfirm = viewModel::confirmDeleteAccount,
        onDeleteAccountDialogDismiss = viewModel::dismissDeleteAccountDialog,
        onDeleteAccountStateConsumed = viewModel::resetDeleteAccountState
    )
}
