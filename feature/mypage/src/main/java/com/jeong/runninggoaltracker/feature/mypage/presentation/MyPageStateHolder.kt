package com.jeong.runninggoaltracker.feature.mypage.presentation

import com.jeong.runninggoaltracker.domain.usecase.GetRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningSummaryUseCase
import com.jeong.runninggoaltracker.domain.usecase.ObserveIsAnonymousUseCase
import com.jeong.runninggoaltracker.domain.usecase.ObserveUserNicknameUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MyPageStateHolder @Inject constructor(
    private val getRunningSummaryUseCase: GetRunningSummaryUseCase,
    private val getRunningGoalUseCase: GetRunningGoalUseCase,
    private val observeIsAnonymousUseCase: ObserveIsAnonymousUseCase,
    private val observeUserNicknameUseCase: ObserveUserNicknameUseCase
) {
    fun uiState(
        localState: StateFlow<MyPageUiState>,
        scope: CoroutineScope
    ): StateFlow<MyPageUiState> =
        combine(
            localState,
            getRunningSummaryUseCase(),
            getRunningGoalUseCase(),
            observeIsAnonymousUseCase(),
            observeUserNicknameUseCase()
        ) { local, summary, goal, isAnonymous, nickname ->
            local.copy(
                isLoading = false,
                summary = summary,
                goal = goal,
                isAnonymous = isAnonymous,
                userNickname = nickname
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = localState.value
        )
}
