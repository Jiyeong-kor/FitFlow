package com.jeong.fitflow.data.di

import android.content.Context
import com.jeong.fitflow.data.local.RunningDatabase
import com.jeong.fitflow.data.local.RunningDatabaseFactory
import com.jeong.fitflow.data.local.RunningGoalDao
import com.jeong.fitflow.data.local.RunningRecordDao
import com.jeong.fitflow.data.local.RunningReminderDao
import com.jeong.fitflow.data.local.WorkoutRecordDao
import com.jeong.fitflow.data.local.SyncOutboxDao
import com.jeong.fitflow.data.repository.AuthRepositoryImpl
import com.jeong.fitflow.data.repository.RunningGoalRepositoryImpl
import com.jeong.fitflow.data.repository.RunningRecordRepositoryImpl
import com.jeong.fitflow.data.repository.RunningReminderRepositoryImpl
import com.jeong.fitflow.data.repository.UserDataSyncRepositoryImpl
import com.jeong.fitflow.data.repository.WorkoutRecordRepositoryImpl
import com.jeong.fitflow.data.util.SystemDateProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.fitflow.domain.di.IoDispatcher
import com.jeong.fitflow.domain.repository.AuthRepository
import com.jeong.fitflow.domain.repository.RunningGoalRepository
import com.jeong.fitflow.domain.repository.RunningRecordRepository
import com.jeong.fitflow.domain.repository.RunningReminderRepository
import com.jeong.fitflow.domain.repository.UserDataSyncRepository
import com.jeong.fitflow.domain.repository.WorkoutRecordRepository
import com.jeong.fitflow.domain.usecase.RunningSummaryCalculator
import com.jeong.fitflow.domain.usecase.WeeklySummaryCalculator
import com.jeong.fitflow.domain.util.DateProvider
import dagger.Binds
import kotlinx.coroutines.CoroutineDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(
        @ApplicationContext context: Context,
        factory: RunningDatabaseFactory,
    ): RunningDatabase = factory.create(context)

    @Provides
    fun provideRunningRecordDao(db: RunningDatabase): RunningRecordDao = db.runningRecordDao()

    @Provides
    fun provideRunningGoalDao(db: RunningDatabase): RunningGoalDao = db.runningGoalDao()

    @Provides
    fun provideRunningReminderDao(db: RunningDatabase): RunningReminderDao = db.runningReminderDao()

    @Provides
    fun provideWorkoutRecordDao(db: RunningDatabase): WorkoutRecordDao = db.workoutRecordDao()

    @Provides
    fun provideSyncOutboxDao(db: RunningDatabase): SyncOutboxDao = db.syncOutboxDao()

    @Provides
    @Singleton
    fun provideDateProvider(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): DateProvider = SystemDateProvider(context, ioDispatcher)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindsModule {

    @Binds
    @Singleton
    abstract fun bindRunningSummaryCalculator(impl: WeeklySummaryCalculator): RunningSummaryCalculator

    @Binds
    @Singleton
    abstract fun bindRunningGoalRepository(impl: RunningGoalRepositoryImpl): RunningGoalRepository

    @Binds
    @Singleton
    abstract fun bindRunningRecordRepository(impl: RunningRecordRepositoryImpl): RunningRecordRepository

    @Binds
    @Singleton
    abstract fun bindRunningReminderRepository(impl: RunningReminderRepositoryImpl): RunningReminderRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRecordRepository(impl: WorkoutRecordRepositoryImpl): WorkoutRecordRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserDataSyncRepository(impl: UserDataSyncRepositoryImpl): UserDataSyncRepository
}
