package com.jeong.runninggoaltracker.domain.repository

import com.jeong.runninggoaltracker.domain.model.RunningRecord
import kotlinx.coroutines.flow.Flow

interface RunningRecordRepository {
    fun getAllRecords(): Flow<List<RunningRecord>>
    suspend fun addRecord(record: RunningRecord): Long
}
