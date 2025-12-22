package com.jeong.runninggoaltracker.feature.record.di

import android.content.Context
import com.jeong.runninggoaltracker.domain.repository.RunningRecordRepository
import com.jeong.runninggoaltracker.domain.usecase.AddRunningRecordUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRecordsUseCase
import com.jeong.runninggoaltracker.feature.record.recognition.ActivityRecognitionController
import com.jeong.runninggoaltracker.feature.record.recognition.ActivityRecognitionManager
import com.jeong.runninggoaltracker.feature.record.recognition.ActivityRecognitionMonitor
import com.jeong.runninggoaltracker.feature.record.recognition.ActivityRecognitionMonitorHolder
import com.jeong.runninggoaltracker.feature.record.recognition.ActivityRecognitionStateHolder
import com.jeong.runninggoaltracker.feature.record.tracking.RunningTrackerController
import com.jeong.runninggoaltracker.feature.record.tracking.RunningTrackerManager
import com.jeong.runninggoaltracker.feature.record.tracking.RunningTrackerMonitor
import com.jeong.runninggoaltracker.feature.record.tracking.RunningTrackerStateHolder
import com.jeong.runninggoaltracker.feature.record.tracking.RunningTrackerStateUpdater
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class RecordBindingsModule {

    @Binds
    @ViewModelScoped
    abstract fun bindActivityRecognitionController(
        manager: ActivityRecognitionManager
    ): ActivityRecognitionController
}

@Module
@InstallIn(SingletonComponent::class)
object RecordSingletonModule {

    @Provides
    @Singleton
    fun provideActivityRecognitionStateHolder(): ActivityRecognitionStateHolder =
        ActivityRecognitionStateHolder()

    @Provides
    @Singleton
    fun provideActivityRecognitionManager(
        @ApplicationContext context: Context,
        stateHolder: ActivityRecognitionStateHolder
    ): ActivityRecognitionManager = ActivityRecognitionManager(
        context = context,
        activityStateUpdater = stateHolder
    )

    @Provides
    fun provideAddRunningRecordUseCase(
        repository: RunningRecordRepository
    ): AddRunningRecordUseCase = AddRunningRecordUseCase(repository)

    @Provides
    fun provideGetRunningRecordsUseCase(
        repository: RunningRecordRepository
    ): GetRunningRecordsUseCase = GetRunningRecordsUseCase(repository)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RecordSingletonBindingsModule {

    @Binds
    @Singleton
    abstract fun bindActivityRecognitionMonitor(
        holder: ActivityRecognitionMonitorHolder
    ): ActivityRecognitionMonitor
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class RunningTrackerControllerBindingsModule {

    @Binds
    @ViewModelScoped
    abstract fun bindRunningTrackerController(
        impl: RunningTrackerManager
    ): RunningTrackerController
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RunningTrackerStateBindingsModule {

    @Binds
    @Singleton
    abstract fun bindRunningTrackerMonitor(
        holder: RunningTrackerStateHolder
    ): RunningTrackerMonitor

    @Binds
    @Singleton
    abstract fun bindRunningTrackerStateUpdater(
        holder: RunningTrackerStateHolder
    ): RunningTrackerStateUpdater
}
