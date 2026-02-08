package com.jeong.runninggoaltracker.feature.mypage.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val deleteAccountHandler: DeleteAccountHandler,
    stateHolder: MyPageStateHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> =
        stateHolder.uiState(_uiState, viewModelScope)

    private val _deleteAccountState =
        MutableStateFlow<DeleteAccountUiState>(DeleteAccountUiState.Idle)
    val deleteAccountState: StateFlow<DeleteAccountUiState> = _deleteAccountState.asStateFlow()

    fun toggleActivityRecognition(isEnabled: Boolean) {
        _uiState.update { it.copy(isActivityRecognitionEnabled = isEnabled) }
    }

    fun showDeleteAccountDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = true) }
    }

    fun dismissDeleteAccountDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = false) }
    }

    fun confirmDeleteAccount() {
        _uiState.update { it.copy(isDeleteDialogVisible = false) }
        deleteAccount()
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            deleteAccountHandler
                .deleteAccount(_deleteAccountState.value)
                .forEach { state ->
                    _deleteAccountState.value = state
                }
        }
    }

    fun resetDeleteAccountState() {
        _deleteAccountState.value = DeleteAccountUiState.Idle
    }
}
