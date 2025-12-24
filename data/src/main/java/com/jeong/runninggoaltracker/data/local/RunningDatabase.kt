package com.jeong.runninggoaltracker.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jeong.runninggoaltracker.data.local.DatabaseConfig.DATABASE_VERSION

object DatabaseConfig {
    const val DATABASE_VERSION = 2
}

@Database(
    entities = [
        RunningRecordEntity::class,
        RunningGoalEntity::class,
        RunningReminderEntity::class
    ],
    version = DATABASE_VERSION,
    autoMigrations = [
        AutoMigration(from = 1, to = DATABASE_VERSION)
    ],
    exportSchema = true
)

@TypeConverters(
    DayOfWeekConverter::class
)

abstract class RunningDatabase : RoomDatabase() {
    abstract fun runningRecordDao(): RunningRecordDao
    abstract fun runningGoalDao(): RunningGoalDao
    abstract fun runningReminderDao(): RunningReminderDao

    companion object {
        const val NAME = "running_goal_tracker.db"
    }
}
