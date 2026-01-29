package com.jeong.runninggoaltracker.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jeong.runninggoaltracker.feature.home.presentation.HomeRoute
import com.jeong.runninggoaltracker.feature.home.presentation.HomeViewModel
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import com.jeong.runninggoaltracker.shared.navigation.composable

fun NavGraphBuilder.homeEntry(
    onNavigateToRecord: () -> Unit,
    onNavigateToGoal: () -> Unit,
    onNavigateToReminder: () -> Unit
) {
    composable<MainNavigationRoute.Home> { backStackEntry ->
        val viewModel: HomeViewModel = hiltViewModel(backStackEntry)
        HomeRoute(
            viewModel = viewModel,
            onNavigateToRecord = onNavigateToRecord,
            onNavigateToGoal = onNavigateToGoal,
            onNavigateToReminder = onNavigateToReminder
        )
    }
}
