package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.repository.UserDataSyncRepository
import javax.inject.Inject

class RestoreUserDataUseCase @Inject constructor(
    private val repository: UserDataSyncRepository
) {
    suspend operator fun invoke(): AuthResult<Unit> = repository.restoreUserDataFromRemote()
}
