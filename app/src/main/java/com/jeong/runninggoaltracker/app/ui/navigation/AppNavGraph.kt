package com.jeong.runninggoaltracker.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.jeong.runninggoaltracker.feature.auth.presentation.OnboardingScreen
import com.jeong.runninggoaltracker.feature.record.api.ActivityRecognitionMonitor
import com.jeong.runninggoaltracker.shared.navigation.AuthRoute
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import com.jeong.runninggoaltracker.shared.navigation.composable
import com.jeong.runninggoaltracker.shared.navigation.navigateTo

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    activityRecognitionMonitor: ActivityRecognitionMonitor,
    requestActivityRecognitionPermission: (onResult: (Boolean) -> Unit) -> Unit,
    requestTrackingPermissions: (onResult: (Boolean) -> Unit) -> Unit,
    requestCameraPermission: (onResult: (Boolean) -> Unit) -> Unit
) {
    val startDestination = if (Firebase.auth.currentUser == null) {
        AuthRoute.Onboarding
    } else {
        MainNavigationRoute.Main
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<AuthRoute.Onboarding> {
            OnboardingScreen(
                onComplete = {
                    navController.navigateTo(MainNavigationRoute.Main) {
                        popUpTo(AuthRoute.Onboarding) { inclusive = true }
                    }
                }
            )
        }
        mainNavGraph(
            activityRecognitionMonitor = activityRecognitionMonitor,
            requestActivityRecognitionPermission = requestActivityRecognitionPermission,
            requestTrackingPermissions = requestTrackingPermissions,
            requestCameraPermission = requestCameraPermission
        )
    }
}
