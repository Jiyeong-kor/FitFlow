package com.jeong.fitflow.feature.record.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RecordRoute(
    onNavigateHome: () -> Unit,
    viewModel: RecordViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecordScreen(
        uiState = uiState,
        onStartActivityRecognition = viewModel::startActivityRecognition,
        onStopActivityRecognition = viewModel::stopActivityRecognition,
        onStartTracking = viewModel::startTracking,
        onStopTracking = viewModel::stopTracking,
        onNavigateHome = onNavigateHome
    )
}
