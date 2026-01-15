package com.jeong.runninggoaltracker.feature.auth.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.usecase.SignInAnonymouslyUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpdateNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jeong.runninggoaltracker.feature.auth.R

enum class OnboardingStep {
    Permissions,
    Nickname,
    Success
}

data class AuthUiState(
    val step: OnboardingStep = OnboardingStep.Permissions,
    val nickname: String = "",
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null,
    val permissionErrorResId: Int? = null,
    val nicknameErrorResId: Int? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase
) : ViewModel() {
    var uiState by mutableStateOf(AuthUiState())
        private set

    fun onPermissionsResult(granted: Boolean) {
        uiState = if (granted) {
            uiState.copy(step = OnboardingStep.Nickname, permissionErrorResId = null)
        } else {
            uiState.copy(
                permissionErrorResId = R.string.permission_required_error
            )
        }
    }

    fun onNicknameChanged(value: String) {
        val trimmed = value.take(MAX_NICKNAME_LENGTH)
        uiState = uiState.copy(nickname = trimmed, errorMessageResId = null, nicknameErrorResId = null)
    }

    fun onContinueWithNickname() {
        val nickname = uiState.nickname.trim()
        if (nickname.isBlank()) {
            uiState = uiState.copy(nicknameErrorResId = R.string.nickname_required_error)
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                errorMessageResId = null,
                nicknameErrorResId = null
            )
            val signInResult = signInAnonymouslyUseCase()
            if (signInResult.isFailure) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessageResId = R.string.anonymous_sign_in_error
                )
                return@launch
            }
            val nicknameResult = updateNicknameUseCase(nickname)
            uiState = if (nicknameResult.isSuccess) {
                uiState.copy(isLoading = false, step = OnboardingStep.Success)
            } else {
                uiState.copy(
                    isLoading = false,
                    errorMessageResId = R.string.nickname_save_error
                )
            }
        }
    }

    companion object {
        private const val MAX_NICKNAME_LENGTH = 10
    }
}
