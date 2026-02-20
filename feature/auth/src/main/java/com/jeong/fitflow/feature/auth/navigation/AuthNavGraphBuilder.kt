package com.jeong.fitflow.feature.auth.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.jeong.fitflow.feature.auth.presentation.OnboardingRoute
import com.jeong.fitflow.feature.auth.presentation.OnboardingViewModel
import com.jeong.fitflow.shared.navigation.AuthRoute
import com.jeong.fitflow.shared.navigation.composable

fun NavGraphBuilder.authEntry(
    onComplete: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    composable<AuthRoute.Onboarding> { backStackEntry ->
        val viewModel: OnboardingViewModel = hiltViewModel(backStackEntry)
        OnboardingRoute(
            viewModel = viewModel,
            onComplete = onComplete,
            onPrivacyPolicyClick = onPrivacyPolicyClick
        )
    }
}
