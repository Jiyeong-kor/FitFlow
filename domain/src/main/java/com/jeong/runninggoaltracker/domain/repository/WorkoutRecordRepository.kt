package com.jeong.runninggoaltracker.domain.repository

import com.jeong.runninggoaltracker.domain.model.WorkoutRecord
import kotlinx.coroutines.flow.Flow

interface WorkoutRecordRepository {
    fun getAllRecords(): Flow<List<WorkoutRecord>>
    suspend fun upsertRecord(record: WorkoutRecord)
}
