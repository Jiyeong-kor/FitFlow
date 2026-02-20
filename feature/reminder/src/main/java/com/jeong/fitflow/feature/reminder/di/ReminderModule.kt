package com.jeong.fitflow.feature.reminder.di

import com.jeong.fitflow.feature.reminder.alarm.ReminderScheduler
import com.jeong.fitflow.feature.reminder.alarm.ReminderSchedulerCoordinator
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
