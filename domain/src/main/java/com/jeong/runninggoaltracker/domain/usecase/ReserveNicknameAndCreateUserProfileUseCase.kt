package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.AuthProvider
import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.repository.AuthRepository
import javax.inject.Inject

class ReserveNicknameAndCreateUserProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        nickname: String,
        authProvider: AuthProvider,
        kakaoOidcSub: String?
    ): AuthResult<Unit> =
        repository.reserveNicknameAndCreateUserProfile(
            nickname = nickname,
            authProvider = authProvider,
            kakaoOidcSub = kakaoOidcSub
        )
}
