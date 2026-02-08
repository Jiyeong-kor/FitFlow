package com.jeong.runninggoaltracker.app.ui.privacy

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PrivacyPolicyUiState())
    val uiState: StateFlow<PrivacyPolicyUiState> = _uiState.asStateFlow()

    fun onRetry() {
        _uiState.update { current ->
            current.copy(
                hasError = false,
                isLoading = true,
                reloadToken = current.reloadToken + 1
            )
        }
    }

    fun onLoadStarted() {
        _uiState.update { current -> current.copy(isLoading = true, hasError = false) }
    }

    fun onLoadFinished() {
        _uiState.update { current -> current.copy(isLoading = false) }
    }

    fun onLoadError() {
        _uiState.update { current -> current.copy(isLoading = false, hasError = true) }
    }

    fun onReloadHandled() {
        _uiState.update { current -> current.copy(lastHandledReloadToken = current.reloadToken) }
    }
}
