package com.jeong.runninggoaltracker.domain.usecase.lunge

import com.jeong.runninggoaltracker.domain.contract.LUNGE_ASCENDING_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.LUNGE_BOTTOM_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.LUNGE_DESCENDING_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.LUNGE_CANDIDATE_DECAY_STEP
import com.jeong.runninggoaltracker.domain.contract.LUNGE_INT_ONE
import com.jeong.runninggoaltracker.domain.contract.LUNGE_INT_ZERO
import com.jeong.runninggoaltracker.domain.contract.LUNGE_REP_COMPLETE_MARGIN
import com.jeong.runninggoaltracker.domain.contract.LUNGE_REP_COMPLETE_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.LUNGE_STANDING_KNEE_ANGLE_THRESHOLD
import com.jeong.runninggoaltracker.domain.contract.LUNGE_STATE_HYSTERESIS_FRAMES
import com.jeong.runninggoaltracker.domain.model.SquatState

data class LungeStateMachineResult(
    val state: SquatState,
    val isRepCompleted: Boolean,
    val standingToDescendingCount: Int,
    val descendingToBottomCount: Int,
    val descendingToStandingCount: Int,
    val bottomToAscendingCount: Int,
    val ascendingToCompleteCount: Int,
    val repCompleteToStandingCount: Int,
    val repCompleteToDescendingCount: Int,
    val hysteresisFrames: Int
)

class LungeStateMachine(
    private val hysteresisFrames: Int = LUNGE_STATE_HYSTERESIS_FRAMES,
    private val standingAngleThreshold: Float = LUNGE_STANDING_KNEE_ANGLE_THRESHOLD,
    private val descendingAngleThreshold: Float = LUNGE_DESCENDING_KNEE_ANGLE_THRESHOLD,
    private val bottomAngleThreshold: Float = LUNGE_BOTTOM_KNEE_ANGLE_THRESHOLD,
    private val ascendingAngleThreshold: Float = LUNGE_ASCENDING_KNEE_ANGLE_THRESHOLD,
    private val repCompleteAngleThreshold: Float = LUNGE_REP_COMPLETE_KNEE_ANGLE_THRESHOLD,
    private val repCompleteMargin: Float = LUNGE_REP_COMPLETE_MARGIN,
    private val debugLogger: (Any) -> Unit = {}
) {
    private var state: SquatState = SquatState.STANDING
    private var standingToDescendingCount: Int = LUNGE_INT_ZERO
    private var descendingToBottomCount: Int = LUNGE_INT_ZERO
    private var descendingToStandingCount: Int = LUNGE_INT_ZERO
    private var bottomToAscendingCount: Int = LUNGE_INT_ZERO
    private var ascendingToCompleteCount: Int = LUNGE_INT_ZERO
    private var repCompleteToStandingCount: Int = LUNGE_INT_ZERO
    private var repCompleteToDescendingCount: Int = LUNGE_INT_ZERO

    fun update(kneeAngleEma: Float, kneeAngleRaw: Float, isReliable: Boolean): LungeStateMachineResult {
        if (!isReliable) {
            return LungeStateMachineResult(
                state = state,
                isRepCompleted = false,
                standingToDescendingCount = standingToDescendingCount,
                descendingToBottomCount = descendingToBottomCount,
                descendingToStandingCount = descendingToStandingCount,
                bottomToAscendingCount = bottomToAscendingCount,
                ascendingToCompleteCount = ascendingToCompleteCount,
                repCompleteToStandingCount = repCompleteToStandingCount,
                repCompleteToDescendingCount = repCompleteToDescendingCount,
                hysteresisFrames = hysteresisFrames
            )
        }
        val descentAngle = minOf(kneeAngleEma, kneeAngleRaw)
        var isRepCompleted = false
        when (state) {
            SquatState.STANDING -> {
                if (applyTransition(
                        isConditionMet = descentAngle <= descendingAngleThreshold,
                        currentCount = standingToDescendingCount,
                        nextState = SquatState.DESCENDING,
                        threshold = descendingAngleThreshold,
                        angleEma = kneeAngleEma,
                        angleRaw = kneeAngleRaw
                    )
                ) {
                    standingToDescendingCount = LUNGE_INT_ZERO
                } else {
                    standingToDescendingCount = updateCount(
                        standingToDescendingCount,
                        descentAngle <= descendingAngleThreshold,
                        isDecayAllowed = false
                    )
                }
            }

            SquatState.DESCENDING -> {
                if (applyTransition(
                        isConditionMet = descentAngle <= bottomAngleThreshold,
                        currentCount = descendingToBottomCount,
                        nextState = SquatState.BOTTOM,
                        threshold = bottomAngleThreshold,
                        angleEma = kneeAngleEma,
                        angleRaw = kneeAngleRaw
                    )
                ) {
                    descendingToBottomCount = LUNGE_INT_ZERO
                } else {
                    descendingToBottomCount = updateCount(
                        descendingToBottomCount,
                        descentAngle <= bottomAngleThreshold,
                        isDecayAllowed = false
                    )
                }
                if (state == SquatState.DESCENDING) {
                    if (applyTransition(
                            isConditionMet = kneeAngleEma >= standingAngleThreshold,
                            currentCount = descendingToStandingCount,
                            nextState = SquatState.STANDING,
                            threshold = standingAngleThreshold,
                            angleEma = kneeAngleEma,
                            angleRaw = kneeAngleRaw
                        )
                    ) {
                        descendingToStandingCount = LUNGE_INT_ZERO
                    } else {
                        descendingToStandingCount = updateCount(
                            descendingToStandingCount,
                            kneeAngleEma >= standingAngleThreshold,
                            isDecayAllowed = false
                        )
                    }
                }
            }

            SquatState.BOTTOM -> {
                if (applyTransition(
                        isConditionMet = kneeAngleEma >= ascendingAngleThreshold,
                        currentCount = bottomToAscendingCount,
                        nextState = SquatState.ASCENDING,
                        threshold = ascendingAngleThreshold,
                        angleEma = kneeAngleEma,
                        angleRaw = kneeAngleRaw
                    )
                ) {
                    bottomToAscendingCount = LUNGE_INT_ZERO
                } else {
                    bottomToAscendingCount = updateCount(
                        bottomToAscendingCount,
                        kneeAngleEma >= ascendingAngleThreshold,
                        isDecayAllowed = false
                    )
                }
            }

            SquatState.ASCENDING -> {
                if (applyTransition(
                        isConditionMet = kneeAngleRaw >= repCompleteAngleThreshold - repCompleteMargin,
                        currentCount = ascendingToCompleteCount,
                        nextState = SquatState.REP_COMPLETE,
                        threshold = repCompleteAngleThreshold - repCompleteMargin,
                        angleEma = kneeAngleEma,
                        angleRaw = kneeAngleRaw
                    )
                ) {
                    isRepCompleted = true
                    ascendingToCompleteCount = LUNGE_INT_ZERO
                } else {
                    ascendingToCompleteCount = updateCount(
                        ascendingToCompleteCount,
                        kneeAngleRaw >= repCompleteAngleThreshold - repCompleteMargin,
                        isDecayAllowed = true
                    )
                }
            }

            SquatState.REP_COMPLETE -> {
                if (applyTransition(
                        isConditionMet = kneeAngleRaw >= standingAngleThreshold,
                        currentCount = repCompleteToStandingCount,
                        nextState = SquatState.STANDING,
                        threshold = standingAngleThreshold,
                        angleEma = kneeAngleEma,
                        angleRaw = kneeAngleRaw
                    )
                ) {
                    repCompleteToStandingCount = LUNGE_INT_ZERO
                } else {
                    repCompleteToStandingCount = updateCount(
                        repCompleteToStandingCount,
                        kneeAngleRaw >= standingAngleThreshold,
                        isDecayAllowed = true
                    )
                }
                if (state == SquatState.REP_COMPLETE) {
                    if (applyTransition(
                            isConditionMet = kneeAngleEma <= descendingAngleThreshold,
                            currentCount = repCompleteToDescendingCount,
                            nextState = SquatState.DESCENDING,
                            threshold = descendingAngleThreshold,
                            angleEma = kneeAngleEma,
                            angleRaw = kneeAngleRaw
                        )
                    ) {
                        repCompleteToDescendingCount = LUNGE_INT_ZERO
                    } else {
                        repCompleteToDescendingCount = updateCount(
                            repCompleteToDescendingCount,
                            kneeAngleEma <= descendingAngleThreshold,
                            isDecayAllowed = false
                        )
                    }
                }
            }
        }
        debugLogger(
            LungeStateMachineDebug(
                state = state,
                kneeAngleEma = kneeAngleEma,
                kneeAngleRaw = kneeAngleRaw,
                standingThreshold = standingAngleThreshold,
                descendingThreshold = descendingAngleThreshold,
                bottomThreshold = bottomAngleThreshold,
                ascendingThreshold = ascendingAngleThreshold,
                repCompleteThreshold = repCompleteAngleThreshold - repCompleteMargin,
                standingToDescendingCount = standingToDescendingCount,
                descendingToBottomCount = descendingToBottomCount,
                descendingToStandingCount = descendingToStandingCount,
                bottomToAscendingCount = bottomToAscendingCount,
                ascendingToCompleteCount = ascendingToCompleteCount,
                repCompleteToStandingCount = repCompleteToStandingCount,
                repCompleteToDescendingCount = repCompleteToDescendingCount,
                isRepCompleted = isRepCompleted
            )
        )
        return LungeStateMachineResult(
            state = state,
            isRepCompleted = isRepCompleted,
            standingToDescendingCount = standingToDescendingCount,
            descendingToBottomCount = descendingToBottomCount,
            descendingToStandingCount = descendingToStandingCount,
            bottomToAscendingCount = bottomToAscendingCount,
            ascendingToCompleteCount = ascendingToCompleteCount,
            repCompleteToStandingCount = repCompleteToStandingCount,
            repCompleteToDescendingCount = repCompleteToDescendingCount,
            hysteresisFrames = hysteresisFrames
        )
    }

    private fun applyTransition(
        isConditionMet: Boolean,
        currentCount: Int,
        nextState: SquatState,
        threshold: Float,
        angleEma: Float,
        angleRaw: Float
    ): Boolean {
        if (!isConditionMet) {
            debugLogger(
                LungeStateMachineTransitionDebug(
                    currentState = state,
                    nextState = nextState,
                    isConditionMet = false,
                    candidateCount = currentCount,
                    threshold = threshold,
                    kneeAngleEma = angleEma,
                    kneeAngleRaw = angleRaw
                )
            )
            return false
        }
        val updated = currentCount + LUNGE_INT_ONE
        debugLogger(
            LungeStateMachineTransitionDebug(
                currentState = state,
                nextState = nextState,
                isConditionMet = true,
                candidateCount = updated,
                threshold = threshold,
                kneeAngleEma = angleEma,
                kneeAngleRaw = angleRaw
            )
        )
        if (updated >= hysteresisFrames) {
            state = nextState
            debugLogger(
                LungeStateMachineStateChangeDebug(
                    state = state,
                    kneeAngleEma = angleEma,
                    kneeAngleRaw = angleRaw
                )
            )
            return true
        }
        return false
    }

    private fun updateCount(currentCount: Int, isConditionMet: Boolean, isDecayAllowed: Boolean): Int =
        if (isConditionMet) {
            (currentCount + LUNGE_INT_ONE).coerceAtMost(hysteresisFrames)
        } else if (isDecayAllowed) {
            (currentCount - LUNGE_CANDIDATE_DECAY_STEP).coerceAtLeast(LUNGE_INT_ZERO)
        } else {
            LUNGE_INT_ZERO
        }
}

private data class LungeStateMachineDebug(
    val state: SquatState,
    val kneeAngleEma: Float,
    val kneeAngleRaw: Float,
    val standingThreshold: Float,
    val descendingThreshold: Float,
    val bottomThreshold: Float,
    val ascendingThreshold: Float,
    val repCompleteThreshold: Float,
    val standingToDescendingCount: Int,
    val descendingToBottomCount: Int,
    val descendingToStandingCount: Int,
    val bottomToAscendingCount: Int,
    val ascendingToCompleteCount: Int,
    val repCompleteToStandingCount: Int,
    val repCompleteToDescendingCount: Int,
    val isRepCompleted: Boolean
)

private data class LungeStateMachineTransitionDebug(
    val currentState: SquatState,
    val nextState: SquatState,
    val isConditionMet: Boolean,
    val candidateCount: Int,
    val threshold: Float,
    val kneeAngleEma: Float,
    val kneeAngleRaw: Float
)

private data class LungeStateMachineStateChangeDebug(
    val state: SquatState,
    val kneeAngleEma: Float,
    val kneeAngleRaw: Float
)
