package com.jeong.runninggoaltracker.feature.auth.presentation

import androidx.annotation.StringRes


enum class OnboardingStep {
    Permissions,
    Nickname,
    Success
}

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.Permissions,
    val nickname: String = "",
    val isLoading: Boolean = false,
    val isNicknameValid: Boolean = false,
    @field:StringRes val nicknameValidationMessage: Int? = null,
    @field:StringRes val nicknameAvailabilityMessageResId: Int? = null,
    val nicknameHintError: Boolean = false,
    @field:StringRes val errorMessageResId: Int? = null,
    @field:StringRes val permissionErrorResId: Int? = null,
    val isPermissionPermanentlyDenied: Boolean = false,
    val showNoInternetDialog: Boolean = false
)

sealed interface OnboardingEffect {
    data object OpenSettings : OnboardingEffect
}
