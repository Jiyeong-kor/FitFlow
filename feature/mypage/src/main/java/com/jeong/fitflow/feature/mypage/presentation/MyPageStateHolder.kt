package com.jeong.fitflow.feature.mypage.presentation

import com.jeong.fitflow.domain.usecase.GetRunningGoalUseCase
import com.jeong.fitflow.domain.usecase.GetRunningSummaryUseCase
import com.jeong.fitflow.domain.usecase.ObserveIsAnonymousUseCase
import com.jeong.fitflow.domain.usecase.ObserveUserNicknameUseCase
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
