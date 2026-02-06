package com.jeong.runninggoaltracker.data.local

import com.jeong.runninggoaltracker.domain.model.WorkoutRecord

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
