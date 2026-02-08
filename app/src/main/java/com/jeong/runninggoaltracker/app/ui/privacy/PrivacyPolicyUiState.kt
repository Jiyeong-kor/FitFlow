package com.jeong.runninggoaltracker.app.ui.privacy

data class PrivacyPolicyUiState(
    val hasError: Boolean = false,
    val isLoading: Boolean = true,
    val reloadToken: Int = 0,
    val lastHandledReloadToken: Int = 0
)
