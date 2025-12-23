package com.jeong.runninggoaltracker.feature.reminder.di

import com.jeong.runninggoaltracker.feature.reminder.alarm.ReminderScheduler
import com.jeong.runninggoaltracker.feature.reminder.alarm.ReminderSchedulerCoordinator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderSingletonModule {

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(
        coordinator: ReminderSchedulerCoordinator
    ): ReminderScheduler
}
