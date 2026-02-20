package com.jeong.fitflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jeong.fitflow.data.contract.RunningDatabaseContract

@Database(
    entities = [
        RunningRecordEntity::class,
        RunningGoalEntity::class,
        RunningReminderEntity::class,
        WorkoutRecordEntity::class,
        SyncOutboxEntity::class
    ],
    version = RunningDatabaseContract.DATABASE_VERSION,
    exportSchema = true
)
@TypeConverters(
    DayOfWeekConverter::class
)
abstract class RunningDatabase : RoomDatabase() {
    abstract fun runningRecordDao(): RunningRecordDao
    abstract fun runningGoalDao(): RunningGoalDao
    abstract fun runningReminderDao(): RunningReminderDao
    abstract fun workoutRecordDao(): WorkoutRecordDao
    abstract fun syncOutboxDao(): SyncOutboxDao
}
