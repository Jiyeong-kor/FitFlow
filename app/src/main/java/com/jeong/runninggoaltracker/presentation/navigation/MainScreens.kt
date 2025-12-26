package com.jeong.runninggoaltracker.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import com.jeong.runninggoaltracker.R
import com.jeong.runninggoaltracker.shared.navigation.BottomTabIcon
import com.jeong.runninggoaltracker.shared.navigation.MainTab
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute

sealed interface MainScreen {
    val route: MainNavigationRoute
    @get:StringRes val titleResId: Int

    data object Home : MainScreen {
        override val route: MainNavigationRoute = MainNavigationRoute.Home
        override val titleResId: Int = R.string.title_home
    }

    data object Record : MainScreen {
        override val route: MainNavigationRoute = MainNavigationRoute.Record
        override val titleResId: Int = R.string.title_record
    }

    data object Goal : MainScreen {
        override val route: MainNavigationRoute = MainNavigationRoute.Goal
        override val titleResId: Int = R.string.title_goal
    }

    data object Reminder : MainScreen {
        override val route: MainNavigationRoute = MainNavigationRoute.Reminder
        override val titleResId: Int = R.string.title_reminder
    }

    companion object {
        private val screenDescriptors: List<MainScreen> = listOf(Home, Record, Goal, Reminder)
        private val screenByRoute: Map<MainNavigationRoute, MainScreen> =
            screenDescriptors.associateBy { it.route }

        val entries: List<MainScreen> = screenDescriptors

        val tabItems: List<MainTabItem> = MainTab.entries.mapNotNull { tab ->
            val screen = screenByRoute[tab.route] ?: return@mapNotNull null
            val icon = tab.icon.asImageVector() ?: return@mapNotNull null
            MainTabItem(
                tab = tab,
                screen = screen,
                bottomNavItem = BottomNavItem(
                    route = screen.route,
                    labelResId = screen.titleResId,
                    icon = icon
                )
            )
        }

        val tabItemsByTab: Map<MainTab, MainTabItem> = tabItems.associateBy { it.tab }

        fun fromDestination(destination: NavDestination?): MainScreen? =
            entries.firstOrNull { screen -> destination.isRouteInHierarchy(screen.route) }
    }
}

private fun BottomTabIcon.asImageVector(): ImageVector? = when (this) {
    BottomTabIcon.HOME -> Icons.Filled.Home
    BottomTabIcon.RECORD -> Icons.AutoMirrored.Filled.DirectionsRun
    BottomTabIcon.REMINDER -> Icons.Filled.Notifications
}

data class MainTabItem(
    val tab: MainTab,
    val screen: MainScreen,
    val bottomNavItem: BottomNavItem
)
