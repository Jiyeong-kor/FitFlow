package com.jeong.fitflow.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.jeong.fitflow.app.ui.privacy.PrivacyPolicyRoute
import com.jeong.fitflow.app.ui.privacy.PrivacyPolicyViewModel
import com.jeong.fitflow.feature.ai_coach.navigation.aiCoachEntry
import com.jeong.fitflow.feature.auth.navigation.authEntry
import com.jeong.fitflow.feature.goal.navigation.goalEntry
import com.jeong.fitflow.feature.home.navigation.homeEntry
import com.jeong.fitflow.feature.mypage.navigation.myPageEntry
import com.jeong.fitflow.feature.record.navigation.recordEntry
import com.jeong.fitflow.feature.reminder.navigation.reminderEntry
import com.jeong.fitflow.shared.navigation.AuthRoute
import com.jeong.fitflow.shared.navigation.MainNavigationRoute
import com.jeong.fitflow.shared.navigation.MainTab
import com.jeong.fitflow.shared.navigation.NavigationRoute
import com.jeong.fitflow.shared.navigation.composable
import com.jeong.fitflow.shared.navigation.navigateTo

@Composable
fun AppNavGraph(
    tabItemsByTab: Map<MainTab, MainTabItem>,
    startDestination: NavigationRoute,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navigationState = rememberMainNavigationState(
        navController = navController,
        tabItemsByTab = tabItemsByTab
    )

    MainContainerRoute(
        navController = navController,
        navigationState = navigationState,
        tabItemsByTab = tabItemsByTab,
        shouldShowNavigationBars = navigationState.currentScreen != null || navigationState.activeTab != null,
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            authEntry(
                onComplete = {
                    navController.navigateTo(MainNavigationRoute.Home) {
                        popUpTo(AuthRoute.Onboarding) { inclusive = true }
                    }
                },
                onPrivacyPolicyClick = {
                    navController.navigateTo(MainNavigationRoute.PrivacyPolicy)
                }
            )
            composable<MainNavigationRoute.PrivacyPolicy> { backStackEntry ->
                val viewModel: PrivacyPolicyViewModel = hiltViewModel(backStackEntry)
                PrivacyPolicyRoute(
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            homeEntry(
                onNavigateToGoal = { navController.navigateTo(MainNavigationRoute.Goal) },
                onNavigateToReminder = { navController.navigateTo(MainNavigationRoute.Reminder) },
                onNavigateToActivityLogs = {
                    navController.navigateTo(MainNavigationRoute.HomeActivityLogs)
                },
                onBackFromActivityLogs = { navController.popBackStack() }
            )
            recordEntry(
                onNavigateHome = {
                    navController.navigateTo(MainNavigationRoute.Home) {
                        popUpTo(MainNavigationRoute.Home) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
            aiCoachEntry(onBack = { navController.popBackStack() })
            goalEntry(onBack = { navController.popBackStack() })
            reminderEntry()
            myPageEntry(
                onNavigateToGoal = { navController.navigateTo(MainNavigationRoute.Goal) },
                onNavigateToReminder = { navController.navigateTo(MainNavigationRoute.Reminder) },
                onNavigateToPrivacyPolicy = {
                    navController.navigateTo(MainNavigationRoute.PrivacyPolicy)
                }
            )
        }
    }
}
