package com.jeong.runninggoaltracker.domain.usecase.squat

import com.jeong.runninggoaltracker.domain.contract.SQUAT_ASCENDING_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.SQUAT_BOTTOM_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.SQUAT_DESCENDING_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.SQUAT_INT_ONE
import com.jeong.runninggoaltracker.domain.contract.SQUAT_INT_ZERO
import com.jeong.runninggoaltracker.domain.contract.SQUAT_REP_COMPLETE_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.SQUAT_STANDING_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.SQUAT_STATE_HYSTERESIS_FRAMES
import com.jeong.runninggoaltracker.domain.model.SquatState

data class SquatStateMachineResult(
    val state: SquatState,
    val repCompleted: Boolean
)

class SquatStateMachine(
    private val hysteresisFrames: Int = SQUAT_STATE_HYSTERESIS_FRAMES,
    private val standingAngleThreshold: Float = SQUAT_STANDING_KNEE_ANGLE_THRESHOLD,
    private val descendingAngleThreshold: Float = SQUAT_DESCENDING_KNEE_ANGLE_THRESHOLD,
    private val bottomAngleThreshold: Float = SQUAT_BOTTOM_KNEE_ANGLE_THRESHOLD,
    private val ascendingAngleThreshold: Float = SQUAT_ASCENDING_KNEE_ANGLE_THRESHOLD,
    private val repCompleteAngleThreshold: Float = SQUAT_REP_COMPLETE_KNEE_ANGLE_THRESHOLD
) {
    private var state: SquatState = SquatState.STANDING
    private var candidateCount: Int = SQUAT_INT_ZERO

    fun update(kneeAngle: Float, isReliable: Boolean): SquatStateMachineResult {
        if (!isReliable) {
            return SquatStateMachineResult(state = state, repCompleted = false)
        }
        var repCompleted = false
        when (state) {
            SquatState.STANDING -> {
                applyTransition(kneeAngle <= descendingAngleThreshold, SquatState.DESCENDING)
            }

            SquatState.DESCENDING -> {
                when {
                    applyTransition(kneeAngle <= bottomAngleThreshold, SquatState.BOTTOM) -> Unit
                    applyTransition(
                        kneeAngle >= standingAngleThreshold,
                        SquatState.STANDING
                    ) -> Unit

                    else -> resetCandidate()
                }
            }

            SquatState.BOTTOM -> {
                applyTransition(kneeAngle >= ascendingAngleThreshold, SquatState.ASCENDING)
            }

            SquatState.ASCENDING -> {
                if (applyTransition(
                        kneeAngle >= repCompleteAngleThreshold,
                        SquatState.REP_COMPLETE
                    )
                ) {
                    repCompleted = true
                }
            }

            SquatState.REP_COMPLETE -> {
                when {
                    applyTransition(
                        kneeAngle >= standingAngleThreshold,
                        SquatState.STANDING
                    ) -> Unit

                    applyTransition(
                        kneeAngle <= descendingAngleThreshold,
                        SquatState.DESCENDING
                    ) -> Unit

                    else -> resetCandidate()
                }
            }
        }
        return SquatStateMachineResult(state = state, repCompleted = repCompleted)
    }

    private fun applyTransition(condition: Boolean, nextState: SquatState): Boolean {
        if (!condition) {
            resetCandidate()
            return false
        }
        candidateCount += SQUAT_INT_ONE
        if (candidateCount >= hysteresisFrames) {
            state = nextState
            resetCandidate()
            return true
        }
        return false
    }

    private fun resetCandidate() {
        candidateCount = SQUAT_INT_ZERO
    }
}
