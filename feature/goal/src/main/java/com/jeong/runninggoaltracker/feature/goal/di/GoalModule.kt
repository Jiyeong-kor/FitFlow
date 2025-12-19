package com.jeong.runninggoaltracker.feature.goal.di

import com.jeong.runninggoaltracker.domain.repository.RunningGoalRepository
import com.jeong.runninggoaltracker.domain.usecase.GetRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpsertRunningGoalUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GoalModule {

    @Provides
    fun provideGetRunningGoalUseCase(
        repository: RunningGoalRepository
    ): GetRunningGoalUseCase = GetRunningGoalUseCase(repository)

    @Provides
    fun provideUpsertRunningGoalUseCase(
        repository: RunningGoalRepository
    ): UpsertRunningGoalUseCase = UpsertRunningGoalUseCase(repository)
}
