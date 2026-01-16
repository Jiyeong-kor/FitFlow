package com.jeong.runninggoaltracker.domain.model

sealed interface AuthError {
    data object NicknameTaken : AuthError
    data object PermissionDenied : AuthError
    data object NetworkError : AuthError
    data object Unknown : AuthError
}
