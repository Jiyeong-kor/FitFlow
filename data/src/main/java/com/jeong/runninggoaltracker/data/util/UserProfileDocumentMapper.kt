package com.jeong.runninggoaltracker.data.util

import com.google.firebase.Timestamp
import com.jeong.runninggoaltracker.data.contract.UserFirestoreFields
import com.jeong.runninggoaltracker.domain.model.AuthProvider

data class UserProfileDocumentPayload(
    val nickname: String,
    val createdAt: Timestamp,
    val lastActiveAt: Timestamp,
    val authProvider: AuthProvider
)

fun UserProfileDocumentPayload.toFirestoreMap(): Map<String, Any> {
    val data = mutableMapOf<String, Any>(
        UserFirestoreFields.NICKNAME to nickname,
        UserFirestoreFields.CREATED_AT to createdAt,
        UserFirestoreFields.LAST_ACTIVE_AT to lastActiveAt,
        UserFirestoreFields.AUTH_PROVIDER to authProvider.rawValue
    )
    return data
}
