package com.jeong.runninggoaltracker.feature.reminder.di

import com.jeong.runninggoaltracker.domain.repository.RunningReminderRepository
import com.jeong.runninggoaltracker.domain.usecase.CreateDefaultReminderUseCase
import com.jeong.runninggoaltracker.domain.usecase.DeleteRunningReminderUseCase
import com.jeong.runninggoaltracker.domain.usecase.GetRunningRemindersUseCase
import com.jeong.runninggoaltracker.domain.usecase.ToggleReminderDayUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpsertRunningReminderUseCase
import com.jeong.runninggoaltracker.feature.reminder.alarm.ReminderSchedulerCoordinator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReminderModule {

    @Provides
    fun provideDeleteRunningReminderUseCase(
        repository: RunningReminderRepository
    ): DeleteRunningReminderUseCase = DeleteRunningReminderUseCase(repository)

    @Provides
    @Singleton
    fun provideGetRunningRemindersUseCase(
        repository: RunningReminderRepository
    ): GetRunningRemindersUseCase = GetRunningRemindersUseCase(repository)

    @Provides
    @Singleton
    fun provideCreateDefaultReminderUseCase(): CreateDefaultReminderUseCase =
        CreateDefaultReminderUseCase()

    @Provides
    @Singleton
    fun provideToggleReminderDayUseCase(): ToggleReminderDayUseCase = ToggleReminderDayUseCase()

    @Provides
    @Singleton
    fun provideUpsertRunningReminderUseCase(
        repository: RunningReminderRepository
    ): UpsertRunningReminderUseCase = UpsertRunningReminderUseCase(repository)

    @Provides
    @Singleton
    fun provideReminderSchedulerCoordinator(
        reminderSchedulerCoordinator: ReminderSchedulerCoordinator
    ): ReminderSchedulerCoordinator = reminderSchedulerCoordinator
}
