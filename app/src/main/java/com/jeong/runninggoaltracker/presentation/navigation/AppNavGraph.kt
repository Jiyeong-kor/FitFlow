package com.jeong.runninggoaltracker.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jeong.runninggoaltracker.presentation.goal.GoalSettingScreen
import com.jeong.runninggoaltracker.presentation.home.HomeScreen
import com.jeong.runninggoaltracker.presentation.record.RecordScreen
import com.jeong.runninggoaltracker.presentation.reminder.ReminderSettingScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                onRecordClick = { navController.navigate("record") },
                onGoalClick = { navController.navigate("goal") },
                onReminderClick = { navController.navigate("reminder") }
            )
        }

        composable("record") {
            RecordScreen()
        }

        composable("goal") {
            GoalSettingScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("reminder") {
            ReminderSettingScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
