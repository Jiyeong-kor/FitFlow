package com.jeong.runninggoaltracker.domain.usecase.squat

import com.jeong.runninggoaltracker.domain.contract.SQUAT_EMA_ALPHA
import com.jeong.runninggoaltracker.domain.contract.SQUAT_FLOAT_ONE
import com.jeong.runninggoaltracker.domain.contract.SQUAT_FLOAT_ZERO

class EmaFilter(
    private val alpha: Float = SQUAT_EMA_ALPHA
) {
    private var hasValue: Boolean = false
    private var value: Float = SQUAT_FLOAT_ZERO

    fun update(input: Float): Float {
        value = if (hasValue) {
            alpha * input + (SQUAT_FLOAT_ONE - alpha) * value
        } else {
            hasValue = true
            input
        }
        return value
    }

    fun current(): Float? = if (hasValue) value else null

}
