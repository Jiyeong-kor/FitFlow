package com.jeong.runninggoaltracker.data.contract

enum class SyncPolicy {
    MUST_SYNC,
    LOCAL_ONLY,
    CONFIG_OPTIONAL
}

data class SyncEntityInfo(
    val roomEntity: String,
    val firestorePath: String,
    val policy: SyncPolicy,
    val conflictStrategy: String
)

object SyncPolicyRegistry {
    val entries = listOf(
        SyncEntityInfo(
            roomEntity = "RunningRecordEntity",
            firestorePath = "users/{uid}/runningRecords/{recordId}",
            policy = SyncPolicy.MUST_SYNC,
            conflictStrategy = "Firestore overwrite after Kakao restore"
        ),
        SyncEntityInfo(
            roomEntity = "RunningGoalEntity",
            firestorePath = "users/{uid}/runningGoals/default",
            policy = SyncPolicy.MUST_SYNC,
            conflictStrategy = "Firestore overwrite after Kakao restore"
        ),
        SyncEntityInfo(
            roomEntity = "RunningReminderEntity",
            firestorePath = "users/{uid}/runningReminders/{reminderId}",
            policy = SyncPolicy.MUST_SYNC,
            conflictStrategy = "Firestore overwrite after Kakao restore"
        ),
        SyncEntityInfo(
            roomEntity = "WorkoutRecordEntity",
            firestorePath = "users/{uid}/workoutRecords/{recordId}",
            policy = SyncPolicy.MUST_SYNC,
            conflictStrategy = "Firestore overwrite after Kakao restore"
        )
    )
}
