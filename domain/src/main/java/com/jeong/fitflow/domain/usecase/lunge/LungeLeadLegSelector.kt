package com.jeong.fitflow.domain.usecase.lunge

import com.jeong.fitflow.domain.contract.LUNGE_LEAD_LEG_WINDOW
import com.jeong.fitflow.domain.model.PoseSide
import java.util.ArrayDeque

class LungeLeadLegSelector(
    private val windowSize: Int = LUNGE_LEAD_LEG_WINDOW
) {
    private val history = ArrayDeque<PoseSide>()
    private var lastSelection: PoseSide? = null

    fun update(
        metrics: LungeRawMetrics?,
        leftKneeAngle: Float?,
        rightKneeAngle: Float?
    ): PoseSide? {
        val currentSelection = metrics?.let {
            leadLeg(it.leftKneeAngle, it.rightKneeAngle)
        } ?: leadLeg(leftKneeAngle, rightKneeAngle) ?: return lastSelection
        history.addLast(currentSelection)
        if (history.size > windowSize) {
            history.removeFirst()
        }
        val leftCount = history.count { it == PoseSide.LEFT }
        val rightCount = history.size - leftCount
        val selected = when {
            leftCount > rightCount -> PoseSide.LEFT
            rightCount > leftCount -> PoseSide.RIGHT
            else -> lastSelection ?: currentSelection
        }
        lastSelection = selected
        return selected
    }

    private fun leadLeg(leftKneeAngle: Float?, rightKneeAngle: Float?): PoseSide? = when {
        leftKneeAngle == null && rightKneeAngle == null -> null
        rightKneeAngle == null -> PoseSide.LEFT
        leftKneeAngle == null -> PoseSide.RIGHT
        leftKneeAngle < rightKneeAngle -> PoseSide.LEFT
        rightKneeAngle < leftKneeAngle -> PoseSide.RIGHT
        else -> lastSelection
    }

    fun reset() {
        history.clear()
        lastSelection = null
    }
}
