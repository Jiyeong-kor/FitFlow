package com.jeong.fitflow.data.local

import com.jeong.fitflow.domain.model.RunningReminder

fun RunningReminderEntity.toDomain(): RunningReminder =
    RunningReminder(
        id = id,
        hour = hour,
        minute = minute,
        isEnabled = isEnabled,
        days = days
    )

fun RunningReminder.toEntity(): RunningReminderEntity =
    RunningReminderEntity(
        id = id,
        hour = hour,
        minute = minute,
        isEnabled = isEnabled,
        days = days
    )
