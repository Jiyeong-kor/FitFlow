package com.jeong.fitflow.feature.goal.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.jeong.fitflow.feature.goal.presentation.GoalRoute
import com.jeong.fitflow.feature.goal.presentation.GoalViewModel
import com.jeong.fitflow.shared.navigation.MainNavigationRoute
import com.jeong.fitflow.shared.navigation.composable

fun NavGraphBuilder.goalEntry(
    onBack: () -> Unit
) {
    composable<MainNavigationRoute.Goal> { backStackEntry ->
        val viewModel: GoalViewModel = hiltViewModel(backStackEntry)
        GoalRoute(
            onBack = onBack,
            viewModel = viewModel
        )
    }
}
