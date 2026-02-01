package com.jeong.runninggoaltracker.feature.auth.presentation

import androidx.annotation.StringRes
import com.jeong.runninggoaltracker.domain.model.AuthError
import com.jeong.runninggoaltracker.domain.usecase.NicknameValidationResult
import com.jeong.runninggoaltracker.feature.auth.R
import com.jeong.runninggoaltracker.feature.auth.domain.OnboardingFailure
import com.jeong.runninggoaltracker.feature.auth.domain.OnboardingResult
import javax.inject.Inject

class OnboardingUiStateReducer @Inject constructor() {

    fun reducePermissionsResult(
        currentState: OnboardingUiState,
        granted: Boolean,
        isPermanentlyDenied: Boolean
    ): OnboardingUiState =
        if (granted) {
            currentState.copy(
                step = OnboardingStep.Nickname,
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

    fun reduceNicknameChanged(currentState: OnboardingUiState, value: String): OnboardingUiState =
        currentState.copy(
            nickname = value,
            isNicknameValid = value.isNotBlank(),
            nicknameValidationMessage = null,
            nicknameAvailabilityMessageResId = null,
            nicknameHintError = false,
            errorMessageResId = null
        )

    fun reduceLoadingStart(currentState: OnboardingUiState): OnboardingUiState =
        currentState.copy(
            isLoading = true,
            errorMessageResId = null,
            nicknameValidationMessage = null,
            nicknameAvailabilityMessageResId = null,
            nicknameHintError = false,
            showNoInternetDialog = false
        )

    fun reduceNoInternetDismissed(currentState: OnboardingUiState): OnboardingUiState =
        currentState.copy(showNoInternetDialog = false)

    fun reduceContinueResult(
        currentState: OnboardingUiState,
        result: OnboardingResult
    ): OnboardingUiState = when (result) {
        is OnboardingResult.Success -> currentState.copy(
            isLoading = false,
            step = OnboardingStep.Success
        )

        is OnboardingResult.ValidationFailed -> {
            val validationUi = result.result.toUiState()
            currentState.copy(
                isLoading = false,
                isNicknameValid = false,
                nicknameValidationMessage = validationUi.messageResId,
                nicknameHintError = validationUi.showHintError
            )
        }

        is OnboardingResult.NoInternet -> currentState.copy(
            isLoading = false,
            showNoInternetDialog = true
        )

        is OnboardingResult.Failure -> when (val reason = result.reason) {
            is OnboardingFailure.AnonymousSignIn -> currentState.copy(
                isLoading = false,
                errorMessageResId = R.string.anonymous_sign_in_error
            )

            is OnboardingFailure.KakaoLogin -> currentState.copy(
                isLoading = false,
                errorMessageResId = R.string.kakao_login_error
            )

            is OnboardingFailure.Auth -> {
                if (reason.error == AuthError.NicknameTaken) {
                    currentState.copy(
                        isLoading = false,
                        isNicknameValid = false,
                        nicknameValidationMessage = R.string.nickname_taken_error,
                        nicknameHintError = false
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

    fun reduceLoginResult(
        currentState: OnboardingUiState,
        result: OnboardingResult
    ): OnboardingUiState = when (result) {
        is OnboardingResult.Success -> currentState.copy(
            isLoading = false,
            step = OnboardingStep.Success
        )

        is OnboardingResult.NoInternet -> currentState.copy(
            isLoading = false,
            showNoInternetDialog = true
        )

        is OnboardingResult.ValidationFailed -> currentState.copy(
            isLoading = false
        )

        is OnboardingResult.Failure -> when (val reason = result.reason) {
            is OnboardingFailure.KakaoLogin -> currentState.copy(
                isLoading = false,
                errorMessageResId = R.string.kakao_login_error
            )

            is OnboardingFailure.AnonymousSignIn -> currentState.copy(
                isLoading = false,
                errorMessageResId = R.string.anonymous_sign_in_error
            )

            is OnboardingFailure.Auth -> currentState.copy(
                isLoading = false,
                errorMessageResId = reason.error.toErrorMessageResId()
            )
        }
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
                showHintError = false
            )

            NicknameValidationResult.Error.EMPTY -> NicknameValidationUiState(
                isValid = false,
                messageResId = R.string.nickname_required_error,
                showHintError = false
            )

            NicknameValidationResult.Error.INVALID_FORMAT -> NicknameValidationUiState(
                isValid = false,
                messageResId = null,
                showHintError = true
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
    val showHintError: Boolean
)
