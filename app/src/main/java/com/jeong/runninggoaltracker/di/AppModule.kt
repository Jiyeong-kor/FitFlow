package com.jeong.runninggoaltracker.di

import android.content.Context
import androidx.room.Room
import com.jeong.runninggoaltracker.data.local.RunningDao
import com.jeong.runninggoaltracker.data.local.RunningDatabase
import com.jeong.runninggoaltracker.data.repository.RunningRepositoryImpl
import com.jeong.runninggoaltracker.domain.repository.RunningRepository
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
}

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {
    @Binds
    @Singleton
    abstract fun bindRunningRepository(impl: RunningRepositoryImpl): RunningRepository
}
