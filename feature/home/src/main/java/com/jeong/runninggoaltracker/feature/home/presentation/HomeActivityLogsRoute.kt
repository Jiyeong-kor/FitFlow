package com.jeong.runninggoaltracker.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeActivityLogsRoute(
    onBack: () -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeActivityLogsScreen(
        activities = uiState.activityLogs,
        onBack = onBack
    )
}
