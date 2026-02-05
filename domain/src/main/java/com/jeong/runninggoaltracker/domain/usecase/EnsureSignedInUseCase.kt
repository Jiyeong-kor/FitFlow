package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.AuthError
import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.repository.AuthRepository
import javax.inject.Inject

class EnsureSignedInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthResult<Unit> =
        if (repository.isSignedIn()) {
            AuthResult.Success(Unit)
        } else {
            repository.signInAnonymously().fold(
                onSuccess = { AuthResult.Success(Unit) },
                onFailure = { AuthResult.Failure(AuthError.PermissionDenied) }
            )
        }
}
