package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.repository.AuthRepository
import javax.inject.Inject

class UpgradeAnonymousWithCustomTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(customToken: String): AuthResult<Unit> =
        repository.upgradeAnonymousWithCustomToken(customToken)
}
