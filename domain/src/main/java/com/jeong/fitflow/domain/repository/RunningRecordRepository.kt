package com.jeong.fitflow.domain.repository

import com.jeong.fitflow.domain.model.RunningRecord
import kotlinx.coroutines.flow.Flow

interface RunningRecordRepository {
    fun getAllRecords(): Flow<List<RunningRecord>>
    suspend fun addRecord(record: RunningRecord): Long
}
