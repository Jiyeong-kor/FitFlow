package com.jeong.runninggoaltracker.data.contract

object UserFirestoreFields {
    const val NICKNAME = "nickname"
    const val CREATED_AT = "createdAt"
    const val LAST_ACTIVE_AT = "lastActiveAt"
    const val AUTH_PROVIDER = "authProvider"
    const val KAKAO_OIDC_SUB = "kakaoOidcSub"
}

object UsernameFirestoreFields {
    const val UID = "uid"
    const val CREATED_AT = "createdAt"
    const val LAST_ACTIVE_AT = "lastActiveAt"
    const val IS_ANONYMOUS = "isAnonymous"
}

object FirestorePaths {
    const val COLLECTION_USERNAMES = "usernames"
    const val COLLECTION_USERS = "users"
    const val COLLECTION_RUNNING_RECORDS = "runningRecords"
    const val COLLECTION_RUNNING_GOALS = "runningGoals"
    const val COLLECTION_RUNNING_REMINDERS = "runningReminders"
    const val COLLECTION_WORKOUT_RECORDS = "workoutRecords"
    const val DOC_RUNNING_GOAL = "default"
    const val FUNCTION_KAKAO_OIDC_EXCHANGE = "exchangeKakaoOidcToken"
}

object RunningRecordFirestoreFields {
    const val DATE = "date"
    const val DISTANCE_KM = "distanceKm"
    const val DURATION_MINUTES = "durationMinutes"
}

object RunningReminderFirestoreFields {
    const val HOUR = "hour"
    const val MINUTE = "minute"
    const val IS_ENABLED = "enabled"
    const val DAYS = "days"
}

object WorkoutRecordFirestoreFields {
    const val DATE = "date"
    const val EXERCISE_TYPE = "exerciseType"
    const val REP_COUNT = "repCount"
}

object RunningGoalFirestoreFields {
    const val WEEKLY_GOAL_KM = "weeklyGoalKm"
}
