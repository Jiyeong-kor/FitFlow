package com.jeong.runninggoaltracker.feature.auth.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.model.AuthError
import com.jeong.runninggoaltracker.domain.usecase.NicknameValidationResult
import com.jeong.runninggoaltracker.feature.auth.domain.OnboardingFailure
import com.jeong.runninggoaltracker.feature.auth.domain.OnboardingResult
import com.jeong.runninggoaltracker.feature.auth.domain.OnboardingWorkflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jeong.runninggoaltracker.feature.auth.R

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

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingWorkflow: OnboardingWorkflow
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState
    private val _effects = MutableSharedFlow<OnboardingEffect>()
    val effects: SharedFlow<OnboardingEffect> = _effects
    private val _isPrivacyAccepted = MutableStateFlow(false)
    val isPrivacyAccepted: StateFlow<Boolean> = _isPrivacyAccepted

    fun onPermissionsResult(granted: Boolean, isPermanentlyDenied: Boolean) =
        _uiState.update { currentState ->
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
        }

    fun onOpenSettings() {
        if (!_uiState.value.isPermissionPermanentlyDenied) {
            return
        }
        viewModelScope.launch {
            _effects.emit(OnboardingEffect.OpenSettings)
        }
    }

    fun onNicknameChanged(value: String) =
        _uiState.update { currentState ->
            currentState.copy(
                nickname = value,
                isNicknameValid = value.isNotBlank(),
                nicknameValidationMessage = null,
                nicknameAvailabilityMessageResId = null,
                nicknameHintError = false,
                errorMessageResId = null
            )
        }

    fun onContinueWithNickname() {
        val nickname = _uiState.value.nickname
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessageResId = null,
                    nicknameValidationMessage = null,
                    nicknameAvailabilityMessageResId = null,
                    nicknameHintError = false,
                    showNoInternetDialog = false
                )
            }
            val result = onboardingWorkflow.continueWithNickname(nickname)
            _uiState.update { currentState ->
                reduceContinueResult(currentState, result)
            }
        }
    }

    fun onRetryInternet() {
        _uiState.update { currentState ->
            currentState.copy(showNoInternetDialog = false)
        }
        onContinueWithNickname()
    }

    fun onDismissNoInternetDialog() =
        _uiState.update { currentState ->
            currentState.copy(showNoInternetDialog = false)
        }

    fun onPrivacyAcceptedChanged(isAccepted: Boolean) {
        _isPrivacyAccepted.value = isAccepted
    }

    fun onKakaoLoginClicked() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessageResId = null,
                    nicknameValidationMessage = null,
                    nicknameAvailabilityMessageResId = null,
                    nicknameHintError = false,
                    showNoInternetDialog = false
                )
            }
            val result = onboardingWorkflow.loginWithKakao()
            _uiState.update { currentState ->
                reduceLoginResult(currentState, result)
            }
        }
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

    private fun reduceContinueResult(
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

    private fun reduceLoginResult(
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
}

private data class NicknameValidationUiState(
    val isValid: Boolean,
    @field:StringRes val messageResId: Int?,
    val showHintError: Boolean
)

sealed interface OnboardingEffect {
    data object OpenSettings : OnboardingEffect
}
