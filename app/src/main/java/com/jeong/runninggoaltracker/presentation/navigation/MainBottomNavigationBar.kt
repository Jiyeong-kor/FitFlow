package com.jeong.runninggoaltracker.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.res.stringResource
import com.jeong.runninggoaltracker.shared.navigation.MainTab

@Composable
fun MainBottomNavigationBar(
    tabItemsByTab: Map<MainTab, MainTabItem>,
    navController: NavHostController,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = colorScheme.surface) {
        MainTab.entries.forEach { tab ->
            val tabItem = tabItemsByTab[tab] ?: return@forEach
            val selected = currentDestination?.hierarchy?.any {
                it.route == tabItem.tab.route.route
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!tabItem.tab.route.isTabDestination) return@NavigationBarItem

                    navController.navigateTo(tabItem.tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = tabItem.bottomNavItem.icon,
                        contentDescription = stringResource(tabItem.bottomNavItem.labelResId)
                    )
                },
                label = {
                    Text(
                        text = stringResource(tabItem.bottomNavItem.labelResId),
                        style = typography.labelSmall
                    )
                }
            )
        }
    }
}
