package com.jeong.runninggoaltracker.feature.auth.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.feature.auth.domain.OnboardingWorkflow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private val onboardingWorkflow: OnboardingWorkflow,
    private val uiStateReducer: OnboardingUiStateReducer
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState
    private val _effects = MutableSharedFlow<OnboardingEffect>()
    val effects: SharedFlow<OnboardingEffect> = _effects
    private val _isPrivacyAccepted = MutableStateFlow(false)
    val isPrivacyAccepted: StateFlow<Boolean> = _isPrivacyAccepted

    fun onPermissionsResult(granted: Boolean, isPermanentlyDenied: Boolean) =
        _uiState.update { currentState ->
            uiStateReducer.reducePermissionsResult(
                currentState = currentState,
                granted = granted,
                isPermanentlyDenied = isPermanentlyDenied
            )
        }

    fun onOpenSettings() {
        viewModelScope.launch {
            uiStateReducer.openSettingsEffects(_uiState.value).forEach { effect ->
                _effects.emit(effect)
            }
        }
    }

    fun onNicknameChanged(value: String) =
        _uiState.update { currentState ->
            uiStateReducer.reduceNicknameChanged(currentState, value)
        }

    fun onContinueWithNickname() {
        val nickname = _uiState.value.nickname
        viewModelScope.launch {
            _uiState.update { currentState ->
                uiStateReducer.reduceLoadingStart(currentState)
            }
            val result = onboardingWorkflow.continueWithNickname(nickname)
            _uiState.update { currentState ->
                uiStateReducer.reduceContinueResult(currentState, result)
            }
        }
    }

    fun onRetryInternet() {
        _uiState.update { currentState ->
            uiStateReducer.reduceNoInternetDismissed(currentState)
        }
        onContinueWithNickname()
    }

    fun onDismissNoInternetDialog() =
        _uiState.update { currentState ->
            uiStateReducer.reduceNoInternetDismissed(currentState)
        }

    fun onPrivacyAcceptedChanged(isAccepted: Boolean) {
        _isPrivacyAccepted.value = isAccepted
    }

    fun onKakaoLoginClicked() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                uiStateReducer.reduceLoadingStart(currentState)
            }
            val result = onboardingWorkflow.loginWithKakao()
            _uiState.update { currentState ->
                uiStateReducer.reduceLoginResult(currentState, result)
            }
        }
    }
}

sealed interface OnboardingEffect {
    data object OpenSettings : OnboardingEffect
}
