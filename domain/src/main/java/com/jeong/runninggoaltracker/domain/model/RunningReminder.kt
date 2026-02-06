package com.jeong.runninggoaltracker.domain.model

data class RunningReminder(
    val id: Int? = null,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean,
    val days: Set<Int>
) {
    fun toggleDay(day: Int): RunningReminder =
        copy(
            days = if (days.contains(day)) {
                days - day
            } else {
                days + day
            }
        )
}
