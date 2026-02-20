package com.jeong.fitflow.feature.record.di

import android.content.Context
import com.jeong.fitflow.feature.record.api.ActivityRecognitionController
import com.jeong.fitflow.feature.record.api.ActivityRecognitionMonitor
import com.jeong.fitflow.feature.record.api.RunningTrackerController
import com.jeong.fitflow.feature.record.api.RunningTrackerMonitor
import com.jeong.fitflow.feature.record.recognition.DefaultActivityRecognitionController
import com.jeong.fitflow.feature.record.recognition.ActivityRecognitionStateHolder
import com.jeong.fitflow.feature.record.recognition.ActivityRecognitionMonitorHolder
import com.jeong.fitflow.feature.record.tracking.DefaultRunningTrackerController
import com.jeong.fitflow.feature.record.tracking.RunningTrackerStateHolder
import com.jeong.fitflow.feature.record.tracking.RunningTrackerStateUpdater
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
        controller: DefaultActivityRecognitionController
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
    fun provideActivityRecognitionController(
        @ApplicationContext context: Context,
        stateHolder: ActivityRecognitionStateHolder
    ): DefaultActivityRecognitionController = DefaultActivityRecognitionController(
        context = context,
        activityStateUpdater = stateHolder
    )
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
        impl: DefaultRunningTrackerController
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
