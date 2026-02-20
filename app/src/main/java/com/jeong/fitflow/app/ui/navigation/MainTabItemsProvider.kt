package com.jeong.fitflow.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.jeong.fitflow.shared.designsystem.icon.AppIcons
import com.jeong.fitflow.shared.navigation.BottomTabIcon
import com.jeong.fitflow.shared.navigation.MainTab
import javax.inject.Inject

interface MainTabItemsProvider {
    fun tabItemsByTab(): Map<MainTab, MainTabItem>
}

class DefaultMainTabItemsProvider @Inject constructor() : MainTabItemsProvider {
    override fun tabItemsByTab(): Map<MainTab, MainTabItem> =
        MainTab.entries.mapNotNull { tab ->
            val screen = MainScreen.fromRoute(tab.route) ?: return@mapNotNull null
            val icon = tab.icon.asPainter()

            MainTabItem(
                tab = tab,
                titleResId = screen.titleResId,
                icon = icon
            )
        }.associateBy { it.tab }
}

fun BottomTabIcon.asPainter(): @Composable () -> Painter = when (this) {
    BottomTabIcon.HOME -> AppIcons::home
    BottomTabIcon.RECORD -> AppIcons::directionsRun
    BottomTabIcon.AI_COACH -> AppIcons::fitnessCenter
    BottomTabIcon.REMINDER -> AppIcons::notifications
    BottomTabIcon.MYPAGE -> AppIcons::accountCircle
}
