package com.jeong.fitflow.domain.repository

import com.jeong.fitflow.domain.model.AuthProvider
import com.jeong.fitflow.domain.model.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun reserveNicknameAndCreateUserProfile(
        nickname: String,
        authProvider: AuthProvider
    ): AuthResult<Unit>

    suspend fun checkNicknameAvailability(nickname: String): AuthResult<Boolean>
    suspend fun deleteAccountAndReleaseNickname(): AuthResult<Unit>
    fun isSignedIn(): Boolean
    fun observeIsAnonymous(): Flow<Boolean>
    fun observeUserNickname(): Flow<String?>
}
