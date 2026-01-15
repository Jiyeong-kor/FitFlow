package com.jeong.runninggoaltracker.shared.navigation

sealed interface AuthRoute : NavigationRoute {
    data object Onboarding : AuthRoute
}
