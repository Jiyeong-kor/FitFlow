package com.jeong.runninggoaltracker.feature.auth.presentation

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

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingWorkflow: OnboardingWorkflow,
    private val uiStateMapper: OnboardingUiStateMapper
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState
    private val _effects = MutableSharedFlow<OnboardingEffect>()
    val effects: SharedFlow<OnboardingEffect> = _effects
    private val _isPrivacyAccepted = MutableStateFlow(false)
    val isPrivacyAccepted: StateFlow<Boolean> = _isPrivacyAccepted

    fun onPermissionsResult(isGranted: Boolean, isPermanentlyDenied: Boolean) {
        _uiState.update { currentState ->
            uiStateMapper.applyPermissionsResult(
                currentState = currentState,
                isGranted = isGranted,
                isPermanentlyDenied = isPermanentlyDenied
            )
        }
    }

    fun onOpenSettings() {
        viewModelScope.launch {
            uiStateMapper.openSettingsEffects(_uiState.value).forEach { effect ->
                _effects.emit(effect)
            }
        }
    }

    fun onNicknameChanged(value: String) {
        _uiState.update { currentState ->
            uiStateMapper.applyNicknameChanged(currentState, value)
        }
    }

    fun onContinueWithNickname() {
        val nickname = _uiState.value.nickname
        val authProvider = _uiState.value.authProvider
        viewModelScope.launch {
            _uiState.update { currentState ->
                uiStateMapper.applyLoadingStart(currentState)
            }
            val result = onboardingWorkflow.continueWithNickname(
                nickname = nickname,
                authProvider = authProvider
            )
            _uiState.update { currentState ->
                uiStateMapper.applyContinueResult(currentState, result)
            }
        }
    }

    fun onRetryInternet() {
        _uiState.update { currentState ->
            uiStateMapper.applyNoInternetDismissed(currentState)
        }
        onContinueWithNickname()
    }

    fun onDismissNoInternetDialog() {
        _uiState.update { currentState ->
            uiStateMapper.applyNoInternetDismissed(currentState)
        }
    }

    fun onPrivacyAcceptedChanged(isAccepted: Boolean) {
        _isPrivacyAccepted.value = isAccepted
    }

    fun onContinueWithoutLogin() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                uiStateMapper.applyLoadingStart(currentState)
            }
            val result = onboardingWorkflow.startAnonymousSession()
            _uiState.update { currentState ->
                uiStateMapper.applyAuthChoiceResult(currentState, result)
            }
        }
    }

}
