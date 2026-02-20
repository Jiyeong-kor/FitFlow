package com.jeong.fitflow.feature.auth.presentation

import androidx.annotation.StringRes
import com.jeong.fitflow.domain.model.AuthProvider


enum class OnboardingStep {
    Permissions,
    AuthChoice,
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
    val shouldShowNicknameHintError: Boolean = false,
    @field:StringRes val errorMessageResId: Int? = null,
    @field:StringRes val permissionErrorResId: Int? = null,
    val isPermissionPermanentlyDenied: Boolean = false,
    val shouldShowNoInternetDialog: Boolean = false,
    val authProvider: AuthProvider? = null
)

sealed interface OnboardingEffect {
    data object OpenSettings : OnboardingEffect
}
