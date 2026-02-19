package com.jeong.runninggoaltracker.data.contract

object RunningDatabaseContract {
    const val DATABASE_VERSION = 4
    const val DATABASE_NAME = "running_goal_tracker.db"
    const val TABLE_RUNNING_REMINDER = "running_reminder"
    const val TABLE_RUNNING_GOAL = "running_goal"
    const val TABLE_RUNNING_RECORD = "running_record"
    const val TABLE_WORKOUT_RECORD = "workout_record"
    const val TABLE_SYNC_OUTBOX = "sync_outbox"
    const val EMPTY_DAYS = ""
    const val DAYS_DELIMITER = ","
    const val QUERY_GET_ALL_REMINDERS =
        "SELECT * FROM $TABLE_RUNNING_REMINDER ORDER BY id ASC"
    const val QUERY_DELETE_REMINDER =
        "DELETE FROM $TABLE_RUNNING_REMINDER WHERE id = :reminderId"
    const val QUERY_GET_GOAL = "SELECT * FROM $TABLE_RUNNING_GOAL WHERE id = 0"
    const val QUERY_GET_ALL_RECORDS = "SELECT * FROM $TABLE_RUNNING_RECORD ORDER BY date DESC"
}
