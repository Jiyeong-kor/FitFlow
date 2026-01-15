package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(nickname: String): Result<Unit> =
        repository.updateNickname(nickname)
}
