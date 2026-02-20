package com.jeong.fitflow.shared.logging.di

import com.jeong.fitflow.shared.logging.AppLogger
import com.jeong.fitflow.shared.logging.LogcatAppLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingModule {
    @Binds
    @Singleton
    abstract fun bindAppLogger(logcatAppLogger: LogcatAppLogger): AppLogger
}
