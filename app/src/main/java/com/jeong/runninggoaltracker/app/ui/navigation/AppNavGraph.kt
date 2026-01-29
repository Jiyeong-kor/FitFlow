package com.jeong.runninggoaltracker.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.jeong.runninggoaltracker.app.ui.privacy.PrivacyPolicyRoute
import com.jeong.runninggoaltracker.app.ui.privacy.PrivacyPolicyViewModel
import com.jeong.runninggoaltracker.feature.auth.presentation.OnboardingRoute
import com.jeong.runninggoaltracker.feature.auth.presentation.OnboardingViewModel
import com.jeong.runninggoaltracker.shared.navigation.AuthRoute
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import com.jeong.runninggoaltracker.shared.navigation.composable
import com.jeong.runninggoaltracker.shared.navigation.navigateTo

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: AppNavGraphViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val startDestination = uiState.startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<AuthRoute.Onboarding> { backStackEntry ->
            val viewModel: OnboardingViewModel = hiltViewModel(backStackEntry)
            OnboardingRoute(
                viewModel = viewModel,
                onComplete = {
                    navController.navigateTo(MainNavigationRoute.Main) {
                        popUpTo(AuthRoute.Onboarding) { inclusive = true }
                    }
                },
                onPrivacyPolicyClick = {
                    navController.navigateTo(MainNavigationRoute.PrivacyPolicy)
                }
            )
        }
        composable<MainNavigationRoute.PrivacyPolicy> { backStackEntry ->
            val viewModel: PrivacyPolicyViewModel = hiltViewModel(backStackEntry)
            PrivacyPolicyRoute(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        mainNavGraph(
        )
    }
}
