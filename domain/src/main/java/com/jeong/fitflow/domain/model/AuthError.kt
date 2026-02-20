package com.jeong.fitflow.domain.model

sealed interface AuthError {
    data object NicknameTaken : AuthError
    data object PermissionDenied : AuthError
    data object NetworkError : AuthError
    data object Unknown : AuthError
}
