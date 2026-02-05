package com.jeong.runninggoaltracker.domain.repository

import com.jeong.runninggoaltracker.domain.model.AuthResult

interface UserDataSyncRepository {
    suspend fun restoreUserDataFromRemote(): AuthResult<Unit>
}
