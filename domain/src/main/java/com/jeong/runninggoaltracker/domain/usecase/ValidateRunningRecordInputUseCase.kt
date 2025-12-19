package com.jeong.runninggoaltracker.domain.usecase

sealed interface RunningRecordValidationResult {

    data class Valid(
        val distanceKm: Double,
        val durationMinutes: Int
    ) : RunningRecordValidationResult

    enum class Error : RunningRecordValidationResult {
        INVALID_NUMBER,
        NON_POSITIVE
    }
}

class ValidateRunningRecordInputUseCase {

    operator fun invoke(
        distanceInput: String,
        durationInput: String
    ): RunningRecordValidationResult {
        val distance = distanceInput.toDoubleOrNull()
            ?: return RunningRecordValidationResult.Error.INVALID_NUMBER

        val duration = durationInput.toIntOrNull()
            ?: return RunningRecordValidationResult.Error.INVALID_NUMBER

        return if (distance <= 0.0 || duration <= 0) {
            RunningRecordValidationResult.Error.NON_POSITIVE
        } else {
            RunningRecordValidationResult.Valid(
                distanceKm = distance,
                durationMinutes = duration
            )
        }
    }
}
