package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.repository.AuthRepository
import javax.inject.Inject

class CheckNicknameAvailabilityUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(nickname: String): AuthResult<Boolean> =
        repository.checkNicknameAvailability(nickname)
}
