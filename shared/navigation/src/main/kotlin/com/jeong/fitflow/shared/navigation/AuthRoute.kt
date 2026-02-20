package com.jeong.fitflow.shared.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface AuthRoute : NavigationRoute {

    @Serializable
    data object Onboarding : AuthRoute
}
