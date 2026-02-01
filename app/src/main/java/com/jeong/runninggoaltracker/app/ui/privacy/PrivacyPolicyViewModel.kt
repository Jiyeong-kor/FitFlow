package com.jeong.runninggoaltracker.app.ui.privacy

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PrivacyPolicyUiState(
    val hasError: Boolean = false,
    val isLoading: Boolean = true,
    val reloadToken: Int = 0,
    val lastHandledReloadToken: Int = 0
)

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor(
    private val reducer: PrivacyPolicyUiStateReducer
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrivacyPolicyUiState())
    val uiState: StateFlow<PrivacyPolicyUiState> = _uiState.asStateFlow()

    fun onRetry() {
        _uiState.update { current -> reducer.onRetry(current) }
    }

    fun onLoadStarted() {
        _uiState.update { current -> reducer.onLoadStarted(current) }
    }

    fun onLoadFinished() {
        _uiState.update { current -> reducer.onLoadFinished(current) }
    }

    fun onLoadError() {
        _uiState.update { current -> reducer.onLoadError(current) }
    }

    fun onReloadHandled() {
        _uiState.update { current -> reducer.onReloadHandled(current) }
    }
}

class PrivacyPolicyUiStateReducer @Inject constructor() {
    fun onRetry(current: PrivacyPolicyUiState): PrivacyPolicyUiState =
        current.copy(
            hasError = false,
            isLoading = true,
            reloadToken = current.reloadToken + 1
        )

    fun onLoadStarted(current: PrivacyPolicyUiState): PrivacyPolicyUiState =
        current.copy(isLoading = true, hasError = false)

    fun onLoadFinished(current: PrivacyPolicyUiState): PrivacyPolicyUiState =
        current.copy(isLoading = false)

    fun onLoadError(current: PrivacyPolicyUiState): PrivacyPolicyUiState =
        current.copy(isLoading = false, hasError = true)

    fun onReloadHandled(current: PrivacyPolicyUiState): PrivacyPolicyUiState =
        current.copy(lastHandledReloadToken = current.reloadToken)
}
