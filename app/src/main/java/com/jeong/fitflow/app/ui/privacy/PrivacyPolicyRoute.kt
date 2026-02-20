package com.jeong.fitflow.app.ui.privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PrivacyPolicyRoute(
    onBack: () -> Unit,
    viewModel: PrivacyPolicyViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PrivacyPolicyScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::onRetry,
        onLoadStarted = viewModel::onLoadStarted,
        onLoadFinished = viewModel::onLoadFinished,
        onLoadError = viewModel::onLoadError,
        onReloadHandled = viewModel::onReloadHandled,
        modifier = modifier
    )
}
