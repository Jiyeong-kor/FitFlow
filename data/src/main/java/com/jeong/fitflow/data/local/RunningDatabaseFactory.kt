package com.jeong.fitflow.data.local

import android.content.Context
import androidx.room.Room
import com.jeong.fitflow.data.contract.RunningDatabaseContract
import javax.inject.Inject

class RunningDatabaseFactory @Inject constructor() {

    fun create(context: Context): RunningDatabase =
        Room.databaseBuilder(
            context,
            RunningDatabase::class.java,
            RunningDatabaseContract.DATABASE_NAME
        ).addMigrations(RunningDatabaseMigrations.MIGRATION_4_5)
            .fallbackToDestructiveMigration(false)
            .build()
}
