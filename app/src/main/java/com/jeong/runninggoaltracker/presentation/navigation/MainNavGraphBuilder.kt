package com.jeong.runninggoaltracker.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.jeong.runninggoaltracker.feature.goal.presentation.GoalRoute
import com.jeong.runninggoaltracker.feature.goal.presentation.GoalViewModel
import com.jeong.runninggoaltracker.feature.home.presentation.HomeRoute
import com.jeong.runninggoaltracker.feature.home.presentation.HomeViewModel
import com.jeong.runninggoaltracker.feature.record.api.ActivityRecognitionMonitor
import com.jeong.runninggoaltracker.feature.record.presentation.RecordRoute
import com.jeong.runninggoaltracker.feature.record.viewmodel.RecordViewModel
import com.jeong.runninggoaltracker.feature.reminder.presentation.ReminderRoute
import com.jeong.runninggoaltracker.feature.reminder.presentation.ReminderViewModel
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import kotlinx.coroutines.flow.map

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainNavGraph(
    activityRecognitionMonitor: ActivityRecognitionMonitor,
    requestTrackingPermissions: (onResult: (Boolean) -> Unit) -> Unit
) {
    composable(route = MainNavigationRoute.Main) {
        MainContainerRoute(
            activityRecognitionMonitor = activityRecognitionMonitor,
            requestTrackingPermissions = requestTrackingPermissions
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun NavGraphBuilder.mainDestinations(
    navController: NavHostController,
    activityRecognitionMonitor: ActivityRecognitionMonitor,
    requestTrackingPermissions: (onResult: (Boolean) -> Unit) -> Unit
) {
    composable(route = MainNavigationRoute.Home) { backStackEntry ->
        val viewModel: HomeViewModel = hiltViewModel(backStackEntry)
        HomeRoute(
            viewModel = viewModel,
            activityStateFlow = activityRecognitionMonitor.activityState.map { state ->
                state.toUiState()
            },
            activityLogsFlow = activityRecognitionMonitor.activityLogs.map { entries ->
                entries.map { it.toUiModel() }
            },
            onRecordClick = { navController.navigateTo(MainNavigationRoute.Record) },
            onGoalClick = { navController.navigateTo(MainNavigationRoute.Goal) },
            onReminderClick = { navController.navigateTo(MainNavigationRoute.Reminder) }
        )
    }

    composable(route = MainNavigationRoute.Record) { backStackEntry ->
        val viewModel: RecordViewModel = hiltViewModel(backStackEntry)
        RecordRoute(
            viewModel = viewModel,
            onRequestTrackingPermissions = requestTrackingPermissions
        )
    }

    composable(route = MainNavigationRoute.Goal) { backStackEntry ->
        val viewModel: GoalViewModel = hiltViewModel(backStackEntry)
        GoalRoute(
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
        )
    }

    composable(route = MainNavigationRoute.Reminder) { backStackEntry ->
        val viewModel: ReminderViewModel = hiltViewModel(backStackEntry)
        ReminderRoute(viewModel = viewModel)
    }
}
