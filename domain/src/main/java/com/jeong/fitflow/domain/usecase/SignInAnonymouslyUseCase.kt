package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.repository.AuthRepository
import javax.inject.Inject

class SignInAnonymouslyUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.signInAnonymously()
}
