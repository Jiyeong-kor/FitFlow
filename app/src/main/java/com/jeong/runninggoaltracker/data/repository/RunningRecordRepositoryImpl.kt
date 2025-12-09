// data/src/main/java/com/jeong/runninggoaltracker/data/repository/RunningRecordRepositoryImpl.kt
package com.jeong.runninggoaltracker.data.repository

import com.jeong.runninggoaltracker.data.local.RunningDao
import com.jeong.runninggoaltracker.data.local.toDomain
import com.jeong.runninggoaltracker.data.local.toEntity
import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.repository.RunningRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RunningRecordRepositoryImpl @Inject constructor(
    private val dao: RunningDao
) : RunningRecordRepository {

    override fun getAllRecords(): Flow<List<RunningRecord>> {
        return dao.getAllRecords().map { records ->
            records.map { it.toDomain() }
        }
    }

    override suspend fun addRecord(record: RunningRecord) {
        dao.insertRecord(record.toEntity())
    }
}
