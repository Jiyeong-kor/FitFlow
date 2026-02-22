package com.jeong.fitflow.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jeong.fitflow.data.contract.RunningDatabaseContract

object RunningDatabaseMigrations {
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE ${RunningDatabaseContract.TABLE_SYNC_OUTBOX} " +
                    "ADD COLUMN nextRetryAt INTEGER NOT NULL DEFAULT 0"
            )
        }
    }
}
