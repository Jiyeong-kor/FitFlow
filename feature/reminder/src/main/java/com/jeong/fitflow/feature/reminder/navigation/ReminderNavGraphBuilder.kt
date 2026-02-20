package com.jeong.fitflow.feature.reminder.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.jeong.fitflow.feature.reminder.presentation.ReminderRoute
import com.jeong.fitflow.feature.reminder.presentation.ReminderViewModel
import com.jeong.fitflow.shared.navigation.MainNavigationRoute
import com.jeong.fitflow.shared.navigation.composable

fun NavGraphBuilder.reminderEntry() {
    composable<MainNavigationRoute.Reminder> { backStackEntry ->
        val viewModel: ReminderViewModel = hiltViewModel(backStackEntry)
        ReminderRoute(viewModel = viewModel)
    }
}
