package com.jeong.fitflow.data.local

import com.jeong.fitflow.domain.model.RunningRecord

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
