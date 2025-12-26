package com.jeong.runninggoaltracker.shared.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class MainTabItem(
    val tab: MainTab,
    @param:StringRes val titleResId: Int,
    val icon: ImageVector,
)
