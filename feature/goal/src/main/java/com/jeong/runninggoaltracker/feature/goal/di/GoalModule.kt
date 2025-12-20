package com.jeong.runninggoaltracker.feature.goal.di

import com.jeong.runninggoaltracker.domain.repository.RunningGoalRepository
import com.jeong.runninggoaltracker.domain.usecase.GetRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpsertRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.ValidateWeeklyGoalUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object GoalModule {

    @Provides
    @ViewModelScoped
    fun provideGetRunningGoalUseCase(
        repository: RunningGoalRepository
    ): GetRunningGoalUseCase = GetRunningGoalUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideUpsertRunningGoalUseCase(
        repository: RunningGoalRepository
    ): UpsertRunningGoalUseCase = UpsertRunningGoalUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideValidateWeeklyGoalUseCase(): ValidateWeeklyGoalUseCase = ValidateWeeklyGoalUseCase()
}
