package com.jeong.runninggoaltracker.feature.record.di

import com.jeong.runninggoaltracker.domain.repository.RunningRecordRepository
import com.jeong.runninggoaltracker.domain.usecase.AddRunningRecordUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRecordsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RecordModule {

    @Provides
    fun provideAddRunningRecordUseCase(
        repository: RunningRecordRepository
    ): AddRunningRecordUseCase = AddRunningRecordUseCase(repository)

    @Provides
    fun provideGetRunningRecordsUseCase(
        repository: RunningRecordRepository
    ): GetRunningRecordsUseCase = GetRunningRecordsUseCase(repository)
}
