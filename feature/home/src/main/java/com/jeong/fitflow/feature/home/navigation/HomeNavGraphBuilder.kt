package com.jeong.fitflow.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jeong.fitflow.feature.home.presentation.HomeActivityLogsRoute
import com.jeong.fitflow.feature.home.presentation.HomeRoute
import com.jeong.fitflow.feature.home.presentation.HomeViewModel
import com.jeong.fitflow.shared.navigation.MainNavigationRoute
import com.jeong.fitflow.shared.navigation.composable

fun NavGraphBuilder.homeEntry(
    onNavigateToGoal: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToActivityLogs: () -> Unit,
    onBackFromActivityLogs: () -> Unit
) {
    composable<MainNavigationRoute.Home> { backStackEntry ->
        val viewModel: HomeViewModel = hiltViewModel(backStackEntry)
        HomeRoute(
            viewModel = viewModel,
            onNavigateToGoal = onNavigateToGoal,
            onNavigateToReminder = onNavigateToReminder,
            onNavigateToActivityLogs = onNavigateToActivityLogs
        )
    }

    composable<MainNavigationRoute.HomeActivityLogs> { backStackEntry ->
        val viewModel: HomeViewModel = hiltViewModel(backStackEntry)
        HomeActivityLogsRoute(
            onBack = onBackFromActivityLogs,
            viewModel = viewModel
        )
    }
}
