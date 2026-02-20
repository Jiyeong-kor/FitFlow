package com.jeong.fitflow.domain.usecase.squat

import com.jeong.fitflow.domain.contract.SQUAT_ASCENDING_KNEE_ANGLE_THRESHOLD
import com.jeong.fitflow.domain.contract.SQUAT_BOTTOM_KNEE_ANGLE_THRESHOLD
import com.jeong.fitflow.domain.contract.SQUAT_DESCENDING_KNEE_ANGLE_THRESHOLD
import com.jeong.fitflow.domain.contract.SQUAT_INT_ONE
import com.jeong.fitflow.domain.contract.SQUAT_INT_ZERO
import com.jeong.fitflow.domain.contract.SQUAT_REP_COMPLETE_KNEE_ANGLE_THRESHOLD
import com.jeong.fitflow.domain.contract.SQUAT_STANDING_KNEE_ANGLE_THRESHOLD
import com.jeong.fitflow.domain.contract.SQUAT_STATE_HYSTERESIS_FRAMES
import com.jeong.fitflow.domain.model.SquatState

data class SquatStateMachineResult(
    val state: SquatState,
    val isRepCompleted: Boolean
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
            return SquatStateMachineResult(state = state, isRepCompleted = false)
        }
        var isRepCompleted = false
        when (state) {
            SquatState.STANDING -> {
                isTransitionApplied(kneeAngle <= descendingAngleThreshold, SquatState.DESCENDING)
            }

            SquatState.DESCENDING -> {
                when {
                    kneeAngle <= bottomAngleThreshold -> {
                        isTransitionApplied(true, SquatState.BOTTOM)
                    }

                    kneeAngle >= standingAngleThreshold -> {
                        isTransitionApplied(true, SquatState.STANDING)
                    }

                    else -> resetCandidate()
                }
            }

            SquatState.BOTTOM -> {
                isTransitionApplied(kneeAngle >= ascendingAngleThreshold, SquatState.ASCENDING)
            }

            SquatState.ASCENDING -> {
                if (isTransitionApplied(
                        kneeAngle >= repCompleteAngleThreshold,
                        SquatState.REP_COMPLETE
                    )
                ) {
                    isRepCompleted = true
                }
            }

            SquatState.REP_COMPLETE -> {
                when {
                    kneeAngle >= standingAngleThreshold -> {
                        isTransitionApplied(true, SquatState.STANDING)
                    }

                    kneeAngle <= descendingAngleThreshold -> {
                        isTransitionApplied(true, SquatState.DESCENDING)
                    }

                    else -> resetCandidate()
                }
            }
        }
        return SquatStateMachineResult(state = state, isRepCompleted = isRepCompleted)
    }

    private fun isTransitionApplied(isConditionMet: Boolean, nextState: SquatState): Boolean {
        if (!isConditionMet) {
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
