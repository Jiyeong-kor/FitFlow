package com.jeong.runninggoaltracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.jeong.runninggoaltracker.domain.repository.AuthRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override suspend fun signInAnonymously(): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInAnonymously()
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { error ->
                    continuation.resume(Result.failure(error))
                }
        }

    override suspend fun updateNickname(nickname: String): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                continuation.resume(Result.failure(IllegalStateException("User not signed in.")))
                return@suspendCancellableCoroutine
            }

            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .build()

            user.updateProfile(profileUpdate)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { error ->
                    continuation.resume(Result.failure(error))
                }
        }
}
