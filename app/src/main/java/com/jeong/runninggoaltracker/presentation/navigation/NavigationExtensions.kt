package com.jeong.runninggoaltracker.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.jeong.runninggoaltracker.shared.navigation.NavigationRoute

fun NavHostController.navigateTo(
    route: NavigationRoute,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route.route, builder)
}

fun NavGraphBuilder.composable(
    route: NavigationRoute,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route.route,
        content = content
    )
}

fun NavDestination?.isRouteInHierarchy(route: NavigationRoute): Boolean =
    this?.hierarchy?.any { destination ->
        destination.route == route.route
    } == true
