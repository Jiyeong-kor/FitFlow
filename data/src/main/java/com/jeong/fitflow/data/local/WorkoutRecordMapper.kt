package com.jeong.fitflow.data.local

import com.jeong.fitflow.domain.model.WorkoutRecord

fun WorkoutRecordEntity.toDomain(): WorkoutRecord =
    WorkoutRecord(
        date = date,
        exerciseType = enumValueOf(exerciseType),
        repCount = repCount
    )

fun WorkoutRecord.toEntity(): WorkoutRecordEntity =
    WorkoutRecordEntity(
        date = date,
        exerciseType = exerciseType.name,
        repCount = repCount
    )
