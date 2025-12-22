package com.jeong.runninggoaltracker.feature.record.presentation

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
class RecordScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_empty_state_when_no_records() {
        composeRule.setContent {
            RunningGoalTrackerTheme {
                RecordScreen(
                    uiState = RecordUiState(activityLabel = "UNKNOWN"),
                    onStartActivityRecognition = {},
                    onStopActivityRecognition = {},
                    onPermissionDenied = {},
                    onStartTracking = {},
                    onStopTracking = {},
                    onTrackingPermissionDenied = {},
                    onRequestTrackingPermissions = {}
                )
            }
        }

        composeRule
            .onNodeWithText("저장된 기록이 없습니다.")
            .assertIsDisplayed()
    }

    @Test
    fun displays_saved_records_with_formatted_values() {
        val record = RunningRecord(
            date = LocalDate.of(2024, 2, 1),
            distanceKm = 5.0,
            durationMinutes = 30
        )

        composeRule.setContent {
            RunningGoalTrackerTheme {
                RecordScreen(
                    uiState = RecordUiState(
                        records = listOf(record),
                        activityLabel = "RUNNING"
                    ),
                    onStartActivityRecognition = {},
                    onStopActivityRecognition = {},
                    onPermissionDenied = {},
                    onStartTracking = {},
                    onStopTracking = {},
                    onTrackingPermissionDenied = {},
                    onRequestTrackingPermissions = {}
                )
            }
        }

        composeRule.onNodeWithText("2월 1일 (목)").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("5 km").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("30 분").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun shows_activity_permission_message_when_denied() {
        composeRule.setContent {
            RunningGoalTrackerTheme {
                RecordScreen(
                    uiState = RecordUiState(activityLabel = "NO_PERMISSION"),
                    onStartActivityRecognition = {},
                    onStopActivityRecognition = {},
                    onPermissionDenied = {},
                    onStartTracking = {},
                    onStopTracking = {},
                    onTrackingPermissionDenied = {},
                    onRequestTrackingPermissions = {}
                )
            }
        }

        composeRule
            .onNodeWithText("현재 활동: 활동 권한이 필요합니다")
            .assertIsDisplayed()
    }

    @Test
    fun shows_error_message_when_activity_request_fails() {
        composeRule.setContent {
            RunningGoalTrackerTheme {
                RecordScreen(
                    uiState = RecordUiState(activityLabel = "REQUEST_FAILED"),
                    onStartActivityRecognition = {},
                    onStopActivityRecognition = {},
                    onPermissionDenied = {},
                    onStartTracking = {},
                    onStopTracking = {},
                    onTrackingPermissionDenied = {},
                    onRequestTrackingPermissions = {}
                )
            }
        }

        composeRule
            .onNodeWithText("현재 활동: 활동 인식에 실패했습니다")
            .assertIsDisplayed()
    }
}
