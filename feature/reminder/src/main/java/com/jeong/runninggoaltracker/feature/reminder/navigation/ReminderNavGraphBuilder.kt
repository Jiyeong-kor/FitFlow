package com.jeong.runninggoaltracker.feature.reminder.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.jeong.runninggoaltracker.feature.reminder.presentation.ReminderRoute
import com.jeong.runninggoaltracker.feature.reminder.presentation.ReminderViewModel
import com.jeong.runninggoaltracker.shared.navigation.MainNavigationRoute
import com.jeong.runninggoaltracker.shared.navigation.composable

fun NavGraphBuilder.reminderEntry() {
    composable<MainNavigationRoute.Reminder> { backStackEntry ->
        val viewModel: ReminderViewModel = hiltViewModel(backStackEntry)
        ReminderRoute(viewModel = viewModel)
    }
}
