package com.jeong.runninggoaltracker.feature.mypage.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun MyPageRoute(
    viewModel: MyPageViewModel,
    onNavigateToGoal: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val deleteAccountState by viewModel.deleteAccountState.collectAsState()

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
