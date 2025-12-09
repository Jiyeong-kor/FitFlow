package com.jeong.runninggoaltracker.domain.usecase

import com.jeong.runninggoaltracker.domain.model.RunningRecord
import com.jeong.runninggoaltracker.domain.repository.RunningRepository

class AddRunningRecordUseCase(
    private val repository: RunningRepository) {
    suspend operator fun invoke(date: String, distanceKm: Double, durationMinutes: Int) {
        repository.addRecord(
            RunningRecord(
                date = date,
                distanceKm = distanceKm,
                durationMinutes = durationMinutes
            )
        )
    }
}
