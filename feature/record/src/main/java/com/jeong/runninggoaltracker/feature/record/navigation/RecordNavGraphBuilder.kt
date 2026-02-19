package com.jeong.runninggoaltracker.feature.record.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.jeong.runninggoaltracker.feature.record.presentation.RecordRoute
import com.jeong.runninggoaltracker.feature.record.presentation.RecordViewModel
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import com.jeong.runninggoaltracker.shared.navigation.composable

fun NavGraphBuilder.recordEntry(
    onNavigateHome: () -> Unit
) {
    composable<MainNavigationRoute.Record> { backStackEntry ->
        val viewModel: RecordViewModel = hiltViewModel(backStackEntry)
        RecordRoute(
            onNavigateHome = onNavigateHome,
            viewModel = viewModel
        )
    }
}
