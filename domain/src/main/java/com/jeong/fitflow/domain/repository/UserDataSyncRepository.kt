package com.jeong.fitflow.domain.repository

import com.jeong.fitflow.domain.model.AuthResult

interface UserDataSyncRepository {
    suspend fun restoreUserDataFromRemote(): AuthResult<Unit>
}
