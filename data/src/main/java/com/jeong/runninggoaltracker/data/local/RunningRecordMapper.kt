package com.jeong.runninggoaltracker.data.local

import com.jeong.runninggoaltracker.domain.model.RunningRecord

fun RunningRecordEntity.toDomain(): RunningRecord =
    RunningRecord(
        id = id,
        date = date,
        distanceKm = distanceKm,
        durationMinutes = durationMinutes
    )

fun RunningRecord.toEntity(): RunningRecordEntity =
    RunningRecordEntity(
        id = id,
        date = date,
        distanceKm = distanceKm,
        durationMinutes = durationMinutes
    )
