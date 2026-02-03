package com.jeong.runninggoaltracker.app.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.runninggoaltracker.app.ui.navigation.AppNavGraph
import com.jeong.runninggoaltracker.app.ui.navigation.MainNavigationViewModel

@Composable
fun EntryPointScreen(
    mainNavigationViewModel: MainNavigationViewModel,
    modifier: Modifier = Modifier
) {
    val tabItemsByTab by mainNavigationViewModel.tabItemsByTab.collectAsStateWithLifecycle()
    AppNavGraph(
        tabItemsByTab = tabItemsByTab,
        modifier = modifier
    )
}
