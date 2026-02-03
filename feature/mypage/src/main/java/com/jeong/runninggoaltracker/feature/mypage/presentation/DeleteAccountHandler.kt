package com.jeong.runninggoaltracker.feature.mypage.presentation

import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.usecase.DeleteAccountUseCase
import javax.inject.Inject

class DeleteAccountHandler @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase
) {
    suspend fun deleteAccount(
        currentState: DeleteAccountUiState
    ): List<DeleteAccountUiState> =
        if (currentState is DeleteAccountUiState.Loading) {
            listOf(currentState)
        } else {
            val finalState = when (val result = deleteAccountUseCase()) {
                is AuthResult.Success -> DeleteAccountUiState.Success
                is AuthResult.Failure -> DeleteAccountUiState.Failure(result.error)
            }
            listOf(DeleteAccountUiState.Loading, finalState)
        }
}
