package com.jeong.runninggoaltracker.data.util

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.jeong.runninggoaltracker.domain.model.AuthError

fun Exception.toAuthError(): AuthError = when (this) {
    is FirebaseNetworkException -> AuthError.NetworkError
    is FirebaseFirestoreException -> when (code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> AuthError.PermissionDenied
        FirebaseFirestoreException.Code.UNAVAILABLE -> AuthError.NetworkError
        else -> AuthError.Unknown
    }

    is FirebaseAuthException -> AuthError.PermissionDenied
    else -> AuthError.Unknown
}
