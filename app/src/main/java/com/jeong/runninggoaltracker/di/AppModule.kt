package com.jeong.runninggoaltracker.di

import android.content.Context
import androidx.room.Room
import com.jeong.runninggoaltracker.data.local.RunningDao
import com.jeong.runninggoaltracker.data.local.RunningDatabase
import com.jeong.runninggoaltracker.data.repository.RunningRepositoryImpl
import com.jeong.runninggoaltracker.domain.repository.RunningRepository
import com.jeong.runninggoaltracker.domain.usecase.AddRunningRecordUseCase
import com.jeong.runninggoaltracker.domain.usecase.AddRunningReminderUseCase
import com.jeong.runninggoaltracker.domain.usecase.DeleteRunningReminderUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRemindersUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningSummaryUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpsertRunningGoalUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpsertRunningReminderUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(@ApplicationContext context: Context): RunningDatabase =
        Room.databaseBuilder(
            context,
            RunningDatabase::class.java,
            RunningDatabase.NAME
        ).fallbackToDestructiveMigration(false)
            .build()


    @Provides
    fun provideRunningDao(db: RunningDatabase): RunningDao = db.runningDao()

    @Provides
    fun provideAddRunningRecordUseCase(repository: RunningRepository): AddRunningRecordUseCase =
        AddRunningRecordUseCase(repository)


    @Provides
    fun provideGetRunningSummaryUseCase(repository: RunningRepository): GetRunningSummaryUseCase =
        GetRunningSummaryUseCase(repository)


    @Provides
    fun provideAddRunningReminderUseCase(repository: RunningRepository): AddRunningReminderUseCase =
        AddRunningReminderUseCase(repository)


    @Provides
    fun provideDeleteRunningReminderUseCase(repository: RunningRepository): DeleteRunningReminderUseCase =
        DeleteRunningReminderUseCase(repository)


    @Provides
    fun provideGetRunningGoalUseCase(repository: RunningRepository): GetRunningGoalUseCase =
        GetRunningGoalUseCase(repository)


    @Provides
    fun provideGetRunningRemindersUseCase(repository: RunningRepository): GetRunningRemindersUseCase =
        GetRunningRemindersUseCase(repository)


    @Provides
    fun provideUpsertRunningGoalUseCase(repository: RunningRepository): UpsertRunningGoalUseCase =
        UpsertRunningGoalUseCase(repository)


    @Provides
    fun provideUpsertRunningReminderUseCase(repository: RunningRepository): UpsertRunningReminderUseCase =
        UpsertRunningReminderUseCase(repository)

}

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {
    @Binds
    @Singleton
    abstract fun bindRunningRepository(impl: RunningRepositoryImpl): RunningRepository
}
