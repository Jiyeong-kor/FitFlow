package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.model.KakaoAuthExchange
import com.jeong.runninggoaltracker.domain.repository.AuthRepository
import javax.inject.Inject

class ExchangeKakaoOidcTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthResult<KakaoAuthExchange> =
        repository.exchangeKakaoOidcToken(idToken)
}
