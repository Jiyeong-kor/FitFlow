package com.jeong.runninggoaltracker.domain.repository

import com.jeong.runninggoaltracker.domain.model.WorkoutRecord

interface WorkoutRecordRepository {
    suspend fun upsertRecord(record: WorkoutRecord)
}
