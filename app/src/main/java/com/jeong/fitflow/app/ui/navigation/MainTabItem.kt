package com.jeong.fitflow.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.jeong.fitflow.shared.navigation.MainTab

data class MainTabItem(
    val tab: MainTab,
    @field:StringRes val titleResId: Int,
    val icon: @Composable () -> Painter,
)
