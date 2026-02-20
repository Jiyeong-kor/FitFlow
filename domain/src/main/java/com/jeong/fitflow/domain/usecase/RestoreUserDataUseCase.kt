package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.AuthResult
import com.jeong.fitflow.domain.repository.UserDataSyncRepository
import javax.inject.Inject

class RestoreUserDataUseCase @Inject constructor(
    private val repository: UserDataSyncRepository
) {
    suspend operator fun invoke(): AuthResult<Unit> = repository.restoreUserDataFromRemote()
}
