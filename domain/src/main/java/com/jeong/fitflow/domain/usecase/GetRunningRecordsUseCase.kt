package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.model.RunningRecord
import com.jeong.fitflow.domain.repository.RunningRecordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRunningRecordsUseCase @Inject constructor(
    private val repository: RunningRecordRepository,
) {
    operator fun invoke(): Flow<List<RunningRecord>> = repository.getAllRecords().map { records ->
        records.sortedByDescending { it.date }
    }
}
