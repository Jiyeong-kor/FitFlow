package com.jeong.fitflow.app.ui.navigation

import com.jeong.fitflow.app.di.MainTabItemsModule
import com.jeong.fitflow.shared.navigation.BottomTabIcon
import com.jeong.fitflow.shared.navigation.MainNavigationRoute
import com.jeong.fitflow.shared.navigation.MainTab
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MainTabItemsModule::class]
)
object FakeMainTabItemsModule {
    @Provides
    fun provideFakeMainTabItemsProvider(): MainTabItemsProvider = FakeMainTabItemsProvider()
}

private class FakeMainTabItemsProvider : MainTabItemsProvider {
    override fun tabItemsByTab(): Map<MainTab, MainTabItem> = mapOf(
        MainTab.RECORD to MainTabItem(
            tab = MainTab.RECORD,
            titleResId = MainScreen.fromRoute(MainNavigationRoute.Record)!!.titleResId,
            icon = BottomTabIcon.RECORD.asPainter()
        ),
        MainTab.AI_COACH to MainTabItem(
            tab = MainTab.AI_COACH,
            titleResId = MainScreen.fromRoute(MainNavigationRoute.AiCoach)!!.titleResId,
            icon = BottomTabIcon.AI_COACH.asPainter()
        )
    )
}
