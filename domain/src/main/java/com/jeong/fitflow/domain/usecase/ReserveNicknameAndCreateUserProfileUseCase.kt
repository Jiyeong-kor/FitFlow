package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.AuthProvider
import com.jeong.fitflow.domain.model.AuthResult
import com.jeong.fitflow.domain.repository.AuthRepository
import javax.inject.Inject

class ReserveNicknameAndCreateUserProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        nickname: String,
        authProvider: AuthProvider
    ): AuthResult<Unit> =
        repository.reserveNicknameAndCreateUserProfile(
            nickname = nickname,
            authProvider = authProvider
        )
}
