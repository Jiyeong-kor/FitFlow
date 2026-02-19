package com.jeong.runninggoaltracker.feature.auth.presentation

import androidx.annotation.StringRes
import com.jeong.runninggoaltracker.domain.model.AuthError
import com.jeong.runninggoaltracker.domain.usecase.NicknameValidationResult
import com.jeong.runninggoaltracker.feature.auth.R
import com.jeong.runninggoaltracker.feature.auth.domain.OnboardingFailure
import com.jeong.runninggoaltracker.feature.auth.domain.OnboardingResult
import javax.inject.Inject

class OnboardingUiStateMapper @Inject constructor() {

    fun applyPermissionsResult(
        currentState: OnboardingUiState,
        isGranted: Boolean,
        isPermanentlyDenied: Boolean
    ): OnboardingUiState =
        if (isGranted) {
            currentState.copy(
                step = OnboardingStep.AuthChoice,
                permissionErrorResId = null,
                isPermissionPermanentlyDenied = false
            )
        } else {
            currentState.copy(
                permissionErrorResId = if (isPermanentlyDenied) {
                    R.string.permission_settings_required_error
                } else {
                    R.string.permission_required_error
                },
                isPermissionPermanentlyDenied = isPermanentlyDenied
            )
        }

    fun applyNicknameChanged(currentState: OnboardingUiState, value: String): OnboardingUiState =
        currentState.copy(
            nickname = value,
            isNicknameValid = value.isNotBlank(),
            nicknameValidationMessage = null,
            nicknameAvailabilityMessageResId = null,
            shouldShowNicknameHintError = false,
            errorMessageResId = null
        )

    fun applyLoadingStart(currentState: OnboardingUiState): OnboardingUiState =
        currentState.copy(
            isLoading = true,
            errorMessageResId = null,
            nicknameValidationMessage = null,
            nicknameAvailabilityMessageResId = null,
            shouldShowNicknameHintError = false,
            shouldShowNoInternetDialog = false
        )

    fun applyNoInternetDismissed(currentState: OnboardingUiState): OnboardingUiState =
        currentState.copy(shouldShowNoInternetDialog = false)

    fun applyContinueResult(
        currentState: OnboardingUiState,
        result: OnboardingResult
    ): OnboardingUiState = when (result) {
        is OnboardingResult.Success -> currentState.copy(
            isLoading = false,
            step = OnboardingStep.Success
        )

        is OnboardingResult.AuthReady -> currentState.copy(
            isLoading = false
        )

        is OnboardingResult.ValidationFailed -> {
            val validationUi = result.result.toUiState()
            currentState.copy(
                isLoading = false,
                isNicknameValid = false,
                nicknameValidationMessage = validationUi.messageResId,
                shouldShowNicknameHintError = validationUi.shouldShowHintError
            )
        }

        is OnboardingResult.NoInternet -> currentState.copy(
            isLoading = false,
            shouldShowNoInternetDialog = true
        )

        is OnboardingResult.Failure -> when (val reason = result.reason) {
            is OnboardingFailure.AnonymousSignIn -> currentState.copy(
                isLoading = false,
                errorMessageResId = R.string.anonymous_sign_in_error
            )

            is OnboardingFailure.Auth -> {
                if (reason.error == AuthError.NicknameTaken) {
                    currentState.copy(
                        isLoading = false,
                        isNicknameValid = false,
                        nicknameValidationMessage = R.string.nickname_taken_error,
                        shouldShowNicknameHintError = false
                    )
                } else {
                    currentState.copy(
                        isLoading = false,
                        errorMessageResId = reason.error.toErrorMessageResId()
                    )
                }
            }
        }
    }

    fun applyAuthChoiceResult(
        currentState: OnboardingUiState,
        result: OnboardingResult
    ): OnboardingUiState = when (result) {
        is OnboardingResult.AuthReady -> currentState.copy(
            isLoading = false,
            step = OnboardingStep.Nickname,
            authProvider = result.authProvider,
        )

        is OnboardingResult.NoInternet -> currentState.copy(
            isLoading = false,
            shouldShowNoInternetDialog = true
        )

        is OnboardingResult.Success -> currentState.copy(isLoading = false)

        is OnboardingResult.Failure -> when (val reason = result.reason) {
            is OnboardingFailure.AnonymousSignIn -> currentState.copy(
                isLoading = false,
                errorMessageResId = R.string.anonymous_sign_in_error
            )

            is OnboardingFailure.Auth -> currentState.copy(
                isLoading = false,
                errorMessageResId = reason.error.toErrorMessageResId()
            )
        }

        is OnboardingResult.ValidationFailed -> currentState.copy(isLoading = false)
    }

    fun openSettingsEffects(currentState: OnboardingUiState): List<OnboardingEffect> =
        if (currentState.isPermissionPermanentlyDenied) {
            listOf(OnboardingEffect.OpenSettings)
        } else {
            emptyList()
        }

    private fun NicknameValidationResult.toUiState(): NicknameValidationUiState =
        when (this) {
            is NicknameValidationResult.Valid -> NicknameValidationUiState(
                isValid = true,
                messageResId = null,
                shouldShowHintError = false
            )

            NicknameValidationResult.Error.EMPTY -> NicknameValidationUiState(
                isValid = false,
                messageResId = R.string.nickname_required_error,
                shouldShowHintError = false
            )

            NicknameValidationResult.Error.INVALID_FORMAT -> NicknameValidationUiState(
                isValid = false,
                messageResId = null,
                shouldShowHintError = true
            )
        }

    private fun AuthError.toErrorMessageResId(): Int =
        when (this) {
            AuthError.NicknameTaken -> R.string.nickname_taken_error
            AuthError.PermissionDenied -> R.string.auth_permission_denied_error
            AuthError.NetworkError -> R.string.auth_network_error
            AuthError.Unknown -> R.string.auth_unknown_error
        }
}

private data class NicknameValidationUiState(
    val isValid: Boolean,
    @field:StringRes val messageResId: Int?,
    val shouldShowHintError: Boolean
)
