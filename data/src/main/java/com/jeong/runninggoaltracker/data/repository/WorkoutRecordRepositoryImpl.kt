package com.jeong.runninggoaltracker.data.repository

import com.jeong.runninggoaltracker.data.local.WorkoutRecordDao
import com.jeong.runninggoaltracker.data.local.toEntity
import com.jeong.runninggoaltracker.domain.model.WorkoutRecord
import com.jeong.runninggoaltracker.domain.repository.WorkoutRecordRepository
import javax.inject.Inject

class WorkoutRecordRepositoryImpl @Inject constructor(
    private val workoutRecordDao: WorkoutRecordDao
) : WorkoutRecordRepository {
    override suspend fun upsertRecord(record: WorkoutRecord) {
        workoutRecordDao.upsertRecord(record.toEntity())
    }
}
