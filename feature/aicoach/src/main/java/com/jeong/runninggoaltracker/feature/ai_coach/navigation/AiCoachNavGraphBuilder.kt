package com.jeong.runninggoaltracker.feature.ai_coach.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.jeong.runninggoaltracker.feature.ai_coach.presentation.AiCoachViewModel
import com.jeong.runninggoaltracker.feature.ai_coach.presentation.SmartWorkoutRoute
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import com.jeong.runninggoaltracker.shared.navigation.composable

fun NavGraphBuilder.aiCoachEntry(
    onBack: () -> Unit
) {
    composable<MainNavigationRoute.AiCoach> { backStackEntry ->
        val viewModel: AiCoachViewModel = hiltViewModel(backStackEntry)
        SmartWorkoutRoute(
            onBack = onBack,
            viewModel = viewModel
        )
    }
}
