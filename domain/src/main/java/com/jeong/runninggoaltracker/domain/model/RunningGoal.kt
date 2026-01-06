package com.jeong.runninggoaltracker.domain.model

data class RunningGoal(
    val weeklyGoalKm: Double
)

val RunningGoal.formattedWeeklyGoalKm: String
    get() = weeklyGoalKm.toFormattedDistance()

private fun Double.toFormattedDistance(): String {
    return if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        toString()
    }
}
