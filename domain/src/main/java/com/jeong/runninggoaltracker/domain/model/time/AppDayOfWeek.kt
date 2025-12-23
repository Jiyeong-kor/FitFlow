package com.jeong.runninggoaltracker.domain.model.time

enum class AppDayOfWeek(val value: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    companion object {
        fun of(value: Int): AppDayOfWeek {
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("요일 값은 1~7 사이여야 합니다: $value")
        }
    }
}
