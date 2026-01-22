package com.jeong.runninggoaltracker.domain.usecase.lunge

import com.jeong.runninggoaltracker.domain.contract.LUNGE_FLOAT_ZERO
import com.jeong.runninggoaltracker.domain.contract.LUNGE_KNEE_ANGLE_MAX_JUMP
import com.jeong.runninggoaltracker.domain.contract.LUNGE_KNEE_ANGLE_MAX_VALID
import com.jeong.runninggoaltracker.domain.contract.LUNGE_KNEE_ANGLE_MIN_VALID
import com.jeong.runninggoaltracker.domain.model.LungeKneeAngleOutlierReason
import com.jeong.runninggoaltracker.domain.model.PoseSide
import kotlin.math.abs

class LungeKneeAngleSanitizer {
    fun sanitize(
        rawAngle: Float?,
        lastValidAngle: Float?,
        side: PoseSide
    ): LungeKneeAngleSanitizeResult {
        if (rawAngle == null) {
            return LungeKneeAngleSanitizeResult(angle = null, outlier = null)
        }
        if (rawAngle < LUNGE_KNEE_ANGLE_MIN_VALID) {
            return LungeKneeAngleSanitizeResult(
                angle = null,
                outlier = LungeKneeAngleOutlier(
                    side = side,
                    rawAngle = rawAngle,
                    lastAngle = lastValidAngle,
                    reason = LungeKneeAngleOutlierReason.LOW_RANGE
                )
            )
        }
        if (rawAngle > LUNGE_KNEE_ANGLE_MAX_VALID) {
            return LungeKneeAngleSanitizeResult(
                angle = null,
                outlier = LungeKneeAngleOutlier(
                    side = side,
                    rawAngle = rawAngle,
                    lastAngle = lastValidAngle,
                    reason = LungeKneeAngleOutlierReason.HIGH_RANGE
                )
            )
        }
        val jump = lastValidAngle?.let { abs(rawAngle - it) } ?: LUNGE_FLOAT_ZERO
        if (lastValidAngle != null && jump > LUNGE_KNEE_ANGLE_MAX_JUMP) {
            return LungeKneeAngleSanitizeResult(
                angle = null,
                outlier = LungeKneeAngleOutlier(
                    side = side,
                    rawAngle = rawAngle,
                    lastAngle = lastValidAngle,
                    reason = LungeKneeAngleOutlierReason.JUMP
                )
            )
        }
        return LungeKneeAngleSanitizeResult(angle = rawAngle, outlier = null)
    }
}

data class LungeKneeAngleSanitizeResult(
    val angle: Float?,
    val outlier: LungeKneeAngleOutlier?
)

data class LungeKneeAngleOutlier(
    val side: PoseSide,
    val rawAngle: Float,
    val lastAngle: Float?,
    val reason: LungeKneeAngleOutlierReason
)
