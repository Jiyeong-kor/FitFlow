package com.jeong.runninggoaltracker.app.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jeong.runninggoaltracker.app.ui.navigation.AppNavGraph
import com.jeong.runninggoaltracker.app.ui.navigation.MainTabItem
import com.jeong.runninggoaltracker.shared.navigation.MainTab

@Composable
fun EntryPointScreen(
    tabItemsByTab: Map<MainTab, MainTabItem>,
    modifier: Modifier = Modifier
) {
    AppNavGraph(
        tabItemsByTab = tabItemsByTab,
        modifier = modifier
    )
}
