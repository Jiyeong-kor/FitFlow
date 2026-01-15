package com.jeong.runninggoaltracker.domain.repository

interface AuthRepository {
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun updateNickname(nickname: String): Result<Unit>
}
