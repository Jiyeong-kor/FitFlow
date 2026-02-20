package com.jeong.fitflow.feature.mypage.presentation

import com.jeong.fitflow.domain.model.AuthResult
import com.jeong.fitflow.domain.usecase.DeleteAccountUseCase
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
