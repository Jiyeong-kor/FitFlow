package com.jeong.runninggoaltracker.feature.record.di

import android.content.Context
import com.jeong.runninggoaltracker.domain.repository.RunningRecordRepository
import com.jeong.runninggoaltracker.domain.usecase.AddRunningRecordUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRecordsUseCase
import com.jeong.runninggoaltracker.domain.usecase.ValidateRunningRecordInputUseCase
import com.jeong.runninggoaltracker.feature.record.recognition.ActivityRecognitionManager
import com.jeong.runninggoaltracker.feature.record.recognition.ActivityRecognitionStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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

    @Provides
    fun provideValidateRunningRecordInputUseCase(): ValidateRunningRecordInputUseCase =
        ValidateRunningRecordInputUseCase()

    @Provides
    @Singleton
    fun provideActivityRecognitionStateHolder(): ActivityRecognitionStateHolder =
        ActivityRecognitionStateHolder()

    @Provides
    @Singleton
    fun provideActivityRecognitionManager(
        @ApplicationContext context: Context,
        stateHolder: ActivityRecognitionStateHolder
    ): ActivityRecognitionManager = ActivityRecognitionManager(context, stateHolder)
}
