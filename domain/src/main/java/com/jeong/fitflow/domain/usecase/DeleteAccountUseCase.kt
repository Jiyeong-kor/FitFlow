package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.AuthResult
import com.jeong.fitflow.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthResult<Unit> = repository.deleteAccountAndReleaseNickname()
}
