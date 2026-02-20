package com.jeong.fitflow.domain.model

data class WorkoutRecord(
    val date: Long,
    val exerciseType: ExerciseType,
    val repCount: Int
)
