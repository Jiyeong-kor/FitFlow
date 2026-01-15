package com.jeong.runninggoaltracker.shared.navigation

import kotlinx.serialization.Serializable

@Serializable
enum class BottomTabIcon {
    HOME,
    RECORD,
    AI_COACH,
    REMINDER,
    MYPAGE
}

@Serializable
enum class MainTab(
    val route: MainNavigationRoute,
    val icon: BottomTabIcon
) {
    HOME(MainNavigationRoute.Home, BottomTabIcon.HOME),
    RECORD(MainNavigationRoute.Record, BottomTabIcon.RECORD),
    AI_COACH(MainNavigationRoute.AiCoach, BottomTabIcon.AI_COACH),
    REMINDER(MainNavigationRoute.Reminder, BottomTabIcon.REMINDER),
    MYPAGE(MainNavigationRoute.MyPage, BottomTabIcon.MYPAGE);
}
