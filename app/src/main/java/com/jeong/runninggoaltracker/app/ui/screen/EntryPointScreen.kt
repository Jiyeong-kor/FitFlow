package com.jeong.runninggoaltracker.app.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jeong.runninggoaltracker.app.ui.navigation.AppNavGraph
import com.jeong.runninggoaltracker.app.ui.navigation.MainTabItem
import com.jeong.runninggoaltracker.shared.navigation.MainTab
import com.jeong.runninggoaltracker.shared.navigation.NavigationRoute

@Composable
fun EntryPointScreen(
    tabItemsByTab: Map<MainTab, MainTabItem>,
    startDestination: NavigationRoute,
    modifier: Modifier = Modifier
) {
    AppNavGraph(
        tabItemsByTab = tabItemsByTab,
        startDestination = startDestination,
        modifier = modifier
    )
}
