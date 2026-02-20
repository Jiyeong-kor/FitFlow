package com.jeong.fitflow.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.jeong.fitflow.R
import com.jeong.fitflow.shared.designsystem.common.AppTopBar
import com.jeong.fitflow.shared.navigation.MainTab

@Composable
fun MainContainerRoute(
    navController: NavHostController,
    navigationState: MainNavigationState,
    tabItemsByTab: Map<MainTab, MainTabItem>,
    shouldShowNavigationBars: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            if (shouldShowNavigationBars) {
                AppTopBar(
                    titleResId = navigationState.titleResId,
                    fallbackTitleResId = R.string.app_name_full,
                    onBack = if (navigationState.shouldShowBackInTopBar) {
                        { navController.popBackStack() }
                    } else {
                        null
                    }
                )
            }
        },
        bottomBar = {
            if (shouldShowNavigationBars) {
                BottomAndTopBar(
                    tabItemsByTab = tabItemsByTab,
                    navController = navController
                )
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
