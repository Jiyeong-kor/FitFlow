package com.jeong.runninggoaltracker.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.jeong.runninggoaltracker.presentation.home.HomeScreen
import com.jeong.runninggoaltracker.presentation.record.RecordScreen
import com.jeong.runninggoaltracker.domain.repository.RunningRepository
import com.jeong.runninggoaltracker.presentation.goal.GoalSettingScreen
import com.jeong.runninggoaltracker.presentation.reminder.ReminderSettingScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    repository: RunningRepository
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                repository = repository,
                onRecordClick = { navController.navigate("record") },
                onGoalClick = { navController.navigate("goal") },
                onReminderClick = { navController.navigate("reminder") }
            )
        }

        composable("record") {
            RecordScreen(repository)
        }

        composable("goal") {
            GoalSettingScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable("reminder") {
            ReminderSettingScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
