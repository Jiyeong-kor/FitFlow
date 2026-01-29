package com.jeong.runninggoaltracker.feature.mypage.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.jeong.runninggoaltracker.feature.mypage.presentation.MyPageRoute
import com.jeong.runninggoaltracker.feature.mypage.presentation.MyPageViewModel
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import com.jeong.runninggoaltracker.shared.navigation.composable

fun NavGraphBuilder.myPageEntry(
    onNavigateToGoal: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    composable<MainNavigationRoute.MyPage> { backStackEntry ->
        val viewModel: MyPageViewModel = hiltViewModel(backStackEntry)
        MyPageRoute(
            viewModel = viewModel,
            onNavigateToGoal = onNavigateToGoal,
            onNavigateToReminder = onNavigateToReminder,
            onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy
        )
    }
}
