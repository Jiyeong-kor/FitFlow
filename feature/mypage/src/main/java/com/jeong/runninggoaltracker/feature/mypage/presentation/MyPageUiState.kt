package com.jeong.runninggoaltracker.feature.mypage.presentation

import com.jeong.runninggoaltracker.domain.model.RunningGoal
import com.jeong.runninggoaltracker.domain.model.RunningSummary


data class MyPageUiState(
    val isLoading: Boolean = true,
    val summary: RunningSummary? = null,
    val goal: RunningGoal? = null,
    val isActivityRecognitionEnabled: Boolean = true,
    val isAnonymous: Boolean = false,
    val userNickname: String? = null,
    val userLevel: String? = null
) {
    companion object {
        fun preview(): MyPageUiState = MyPageUiState(
            isLoading = false,
            summary = RunningSummary(
                weeklyGoalKm = 15.0,
                totalThisWeekKm = 9.5,
                recordCountThisWeek = 3,
                progress = 0.63f
            ),
            userNickname = "러너",
            userLevel = "Active Runner",
            isActivityRecognitionEnabled = true,
            isAnonymous = true
        )
    }
}
