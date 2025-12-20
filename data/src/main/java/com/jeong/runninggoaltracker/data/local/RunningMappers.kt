package com.jeong.runninggoaltracker.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import java.time.LocalDate

private val dayOfWeekConverter = DayOfWeekConverter()

@RequiresApi(Build.VERSION_CODES.O)
fun RunningRecordEntity.toDomain(): RunningRecord =
    RunningRecord(
        id = id,
        date = LocalDate.parse(date),
        distanceKm = distanceKm,
        durationMinutes = durationMinutes
    )

fun RunningRecord.toEntity(): RunningRecordEntity =
    RunningRecordEntity(
        id = id,
        date = date.toString(),
        distanceKm = distanceKm,
        durationMinutes = durationMinutes
    )

fun RunningGoalEntity.toDomain(): RunningGoal =
    RunningGoal(
        weeklyGoalKm = weeklyGoalKm
    )

fun RunningGoal.toEntity(): RunningGoalEntity =
    RunningGoalEntity(
        weeklyGoalKm = weeklyGoalKm
    )

@RequiresApi(Build.VERSION_CODES.O)
fun RunningReminderEntity.toDomain(): RunningReminder =
    RunningReminder(
        id = id,
        hour = hour,
        minute = minute,
        enabled = enabled,
        days = dayOfWeekConverter.fromDays(days)
    )

@RequiresApi(Build.VERSION_CODES.O)
fun RunningReminder.toEntity(): RunningReminderEntity =
    RunningReminderEntity(
        id = id,
        hour = hour,
        minute = minute,
        enabled = enabled,
        days = dayOfWeekConverter.toDays(days)
    )
