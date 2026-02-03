package com.jeong.runninggoaltracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.jeong.runninggoaltracker.app.ui.screen.EntryPointRoute
import com.jeong.runninggoaltracker.app.ui.navigation.AppNavGraphViewModel
import com.jeong.runninggoaltracker.app.ui.navigation.MainNavigationViewModel
import com.jeong.runninggoaltracker.feature.record.api.ActivityRecognitionMonitor
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var activityRecognitionMonitor: ActivityRecognitionMonitor
    private val mainNavigationViewModel: MainNavigationViewModel by viewModels()
    private val appNavGraphViewModel: AppNavGraphViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RunningGoalTrackerTheme {
                EntryPointRoute(
                    mainNavigationViewModel = mainNavigationViewModel,
                    appNavGraphViewModel = appNavGraphViewModel
                )
            }
        }
    }
}
