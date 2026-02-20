package com.jeong.fitflow.app.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jeong.fitflow.app.ui.navigation.AppNavGraph
import com.jeong.fitflow.app.ui.navigation.MainTabItem
import com.jeong.fitflow.shared.navigation.MainTab
import com.jeong.fitflow.shared.navigation.NavigationRoute

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
