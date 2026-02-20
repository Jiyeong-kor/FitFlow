package com.jeong.fitflow.shared.navigation

private val mainTabRoutes: Set<MainNavigationRoute> =
    MainTab.entries.map { it.route }.toSet()

fun MainNavigationRoute.isBottomTab(): Boolean =
    this in mainTabRoutes
