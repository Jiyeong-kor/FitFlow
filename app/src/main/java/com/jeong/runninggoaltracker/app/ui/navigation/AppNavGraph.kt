package com.jeong.runninggoaltracker.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.jeong.runninggoaltracker.app.ui.privacy.PrivacyPolicyRoute
import com.jeong.runninggoaltracker.app.ui.privacy.PrivacyPolicyViewModel
import com.jeong.runninggoaltracker.feature.ai_coach.navigation.aiCoachEntry
import com.jeong.runninggoaltracker.feature.auth.navigation.authEntry
import com.jeong.runninggoaltracker.feature.goal.navigation.goalEntry
import com.jeong.runninggoaltracker.feature.home.navigation.homeEntry
import com.jeong.runninggoaltracker.feature.mypage.navigation.myPageEntry
import com.jeong.runninggoaltracker.feature.record.navigation.recordEntry
import com.jeong.runninggoaltracker.feature.reminder.navigation.reminderEntry
import com.jeong.runninggoaltracker.shared.navigation.AuthRoute
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import com.jeong.runninggoaltracker.shared.navigation.MainTab
import com.jeong.runninggoaltracker.shared.navigation.composable
import com.jeong.runninggoaltracker.shared.navigation.navigateTo

@Composable
fun AppNavGraph(
    tabItemsByTab: Map<MainTab, MainTabItem>,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navigationState = rememberMainNavigationState(
        navController = navController,
        tabItemsByTab = tabItemsByTab
    )
    val startDestination = remember {
        if (Firebase.auth.currentUser?.displayName?.isNotBlank() == true) {
            MainNavigationRoute.Home
        } else {
            AuthRoute.Onboarding
        }
    }

    MainContainerRoute(
        navController = navController,
        navigationState = navigationState,
        tabItemsByTab = tabItemsByTab,
        showNavigationBars = navigationState.currentScreen != null || navigationState.activeTab != null,
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
                onNavigateToRecord = { navController.navigateTo(MainNavigationRoute.Record) },
                onNavigateToGoal = { navController.navigateTo(MainNavigationRoute.Goal) },
                onNavigateToReminder = { navController.navigateTo(MainNavigationRoute.Reminder) }
            )
            recordEntry()
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
