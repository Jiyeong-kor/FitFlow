package com.jeong.fitflow.feature.mypage.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.jeong.fitflow.feature.mypage.presentation.MyPageRoute
import com.jeong.fitflow.feature.mypage.presentation.MyPageViewModel
import com.jeong.fitflow.shared.navigation.MainNavigationRoute
import com.jeong.fitflow.shared.navigation.composable

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
