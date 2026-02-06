package com.jeong.runninggoaltracker.data.local

import com.jeong.runninggoaltracker.domain.model.RunningReminder

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
