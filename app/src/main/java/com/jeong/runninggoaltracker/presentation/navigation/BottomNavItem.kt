package com.jeong.runninggoaltracker.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute

data class BottomNavItem(
    val route: MainNavigationRoute,
    @param:StringRes val labelResId: Int,
    val icon: ImageVector
)
