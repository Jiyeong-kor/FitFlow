package com.jeong.runninggoaltracker.domain.repository

import com.jeong.runninggoaltracker.domain.model.AuthProvider
import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.model.KakaoAuthExchange
import com.jeong.runninggoaltracker.domain.model.KakaoOidcToken
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun signInWithKakao(): Result<KakaoOidcToken>
    suspend fun exchangeKakaoOidcToken(idToken: String): AuthResult<KakaoAuthExchange>
    suspend fun signInWithCustomToken(customToken: String): AuthResult<Unit>
    suspend fun reserveNicknameAndCreateUserProfile(
        nickname: String,
        authProvider: AuthProvider,
        kakaoOidcSub: String?
    ): AuthResult<Unit>

    suspend fun checkNicknameAvailability(nickname: String): AuthResult<Boolean>
    suspend fun deleteAccountAndReleaseNickname(): AuthResult<Unit>
    fun isSignedIn(): Boolean
    fun observeIsAnonymous(): Flow<Boolean>
    fun observeUserNickname(): Flow<String?>
}
