package com.jeong.runninggoaltracker.data.local

import com.jeong.runninggoaltracker.domain.model.RunningGoal

fun RunningGoalEntity.toDomain(): RunningGoal =
    RunningGoal(
        weeklyGoalKm = weeklyGoalKm
    )

fun RunningGoal.toEntity(): RunningGoalEntity =
    RunningGoalEntity(
        weeklyGoalKm = weeklyGoalKm
    )
