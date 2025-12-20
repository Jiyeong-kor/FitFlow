package com.jeong.runninggoaltracker.feature.home.di

import com.jeong.runninggoaltracker.domain.repository.RunningGoalRepository
import com.jeong.runninggoaltracker.domain.repository.RunningRecordRepository
import com.jeong.runninggoaltracker.domain.usecase.GetRunningSummaryUseCase
import com.jeong.runninggoaltracker.domain.usecase.RunningSummaryCalculator
import com.jeong.runninggoaltracker.domain.usecase.WeeklySummaryCalculator
import com.jeong.runninggoaltracker.domain.util.DateProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object HomeModule {

    @Provides
    @ViewModelScoped
    fun provideRunningSummaryCalculator(): RunningSummaryCalculator = WeeklySummaryCalculator()

    @Provides
    @ViewModelScoped
    fun provideGetRunningSummaryUseCase(
        goalRepository: RunningGoalRepository,
        recordRepository: RunningRecordRepository,
        dateProvider: DateProvider,
        summaryCalculator: RunningSummaryCalculator
    ): GetRunningSummaryUseCase =
        GetRunningSummaryUseCase(goalRepository, recordRepository, dateProvider, summaryCalculator)
}
