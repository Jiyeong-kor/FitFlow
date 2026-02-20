package com.jeong.fitflow.feature.ai_coach.logging

import com.jeong.fitflow.feature.ai_coach.contract.SmartWorkoutLogContract
import com.jeong.fitflow.shared.logging.AppLogger
import javax.inject.Inject

class SmartWorkoutLogger @Inject constructor(
    private val appLogger: AppLogger
) {
    fun logDebug(message: () -> String) =
        appLogger.debug(SmartWorkoutLogContract.LOG_TAG, message())
}
