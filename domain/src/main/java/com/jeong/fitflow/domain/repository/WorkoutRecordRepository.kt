package com.jeong.fitflow.domain.repository

import com.jeong.fitflow.domain.model.WorkoutRecord
import kotlinx.coroutines.flow.Flow

interface WorkoutRecordRepository {
    fun getAllRecords(): Flow<List<WorkoutRecord>>
    suspend fun upsertRecord(record: WorkoutRecord)
}
