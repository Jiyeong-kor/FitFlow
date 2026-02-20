package com.jeong.fitflow.data.util

import com.google.firebase.firestore.DocumentSnapshot
import com.jeong.fitflow.data.contract.RunningReminderFirestoreFields
import com.jeong.fitflow.data.local.RunningReminderEntity

fun DocumentSnapshot.toRunningReminderEntity(): RunningReminderEntity? {
    val id = id.toIntOrNull() ?: return null
    val hour = getLong(RunningReminderFirestoreFields.HOUR)?.toInt() ?: return null
    val minute = getLong(RunningReminderFirestoreFields.MINUTE)?.toInt() ?: return null
    val isEnabled = getBoolean(RunningReminderFirestoreFields.IS_ENABLED) ?: return null
    val days = get(RunningReminderFirestoreFields.DAYS) as? List<*> ?: emptyList<Any>()
    val daySet = days.mapNotNull { (it as? Number)?.toInt() }.toSet()
    return RunningReminderEntity(
        id = id,
        hour = hour,
        minute = minute,
        isEnabled = isEnabled,
        days = daySet
    )
}
