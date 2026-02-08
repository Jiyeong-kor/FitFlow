package com.jeong.runninggoaltracker.feature.home.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jeong.runninggoaltracker.feature.home.contract.HomeTestTagContract
import com.jeong.runninggoaltracker.feature.home.domain.HomeCalendarCalculator
import com.jeong.runninggoaltracker.shared.designsystem.theme.RunningGoalTrackerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenNavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun viewAllClickInvokesRecordCallback() {
        val calendarCalculator = HomeCalendarCalculator()
        val calendarMonthState = calendarCalculator.monthStateFromMillis(0L)
        val uiState = HomeUiState(
            selectedDateState = SelectedDateState(dateMillis = 0L),
            calendarMonthState = calendarMonthState,
            weeklyRange = HomeWeeklyRange(startMillis = 0L, endMillis = 0L)
        )
        var recordClicks = 0

        composeRule.setContent {
            RunningGoalTrackerTheme {
                HomeScreen(
                    uiState = uiState,
                    onPeriodSelected = {},
                    onNavigatePreviousPeriod = {},
                    onNavigateNextPeriod = {},
                    onDateSelected = {},
                    onCalendarOpen = {},
                    onCalendarDismiss = {},
                    onCalendarPreviousMonth = {},
                    onCalendarNextMonth = {},
                    onRecordClick = { recordClicks += 1 },
                    onGoalClick = {},
                    onReminderClick = {}
                )
            }
        }

        composeRule
            .onNodeWithTag(HomeTestTagContract.HOME_ACTIVITY_LOG_VIEW_ALL_TAG)
            .performClick()

        composeRule.runOnIdle {
            assertEquals(1, recordClicks)
        }
    }
}
