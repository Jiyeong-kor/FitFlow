package com.jeong.fitflow.data.local

import com.jeong.fitflow.domain.model.RunningGoal

fun RunningGoalEntity.toDomain(): RunningGoal =
    RunningGoal(
        weeklyGoalKm = weeklyGoalKm
    )

fun RunningGoal.toEntity(): RunningGoalEntity =
    RunningGoalEntity(
        weeklyGoalKm = weeklyGoalKm
    )
