package com.jeong.runninggoaltracker.shared.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationRoute

@Serializable
sealed interface FeatureNavigationRoute : NavigationRoute

@Serializable
sealed interface MainNavigationRoute : FeatureNavigationRoute {
    val isTabDestination: Boolean

    @Serializable
    data object Main : MainNavigationRoute {
        override val isTabDestination: Boolean = false
    }

    @Serializable
    data object Home : MainNavigationRoute {
        override val isTabDestination: Boolean = true
    }

    @Serializable
    data object Record : MainNavigationRoute {
        override val isTabDestination: Boolean = true
    }

    @Serializable
    data object Goal : MainNavigationRoute {
        override val isTabDestination: Boolean = false
    }

    @Serializable
    data object Reminder : MainNavigationRoute {
        override val isTabDestination: Boolean = true
    }
}
