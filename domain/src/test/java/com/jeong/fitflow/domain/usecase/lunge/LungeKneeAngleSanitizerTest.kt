package com.jeong.fitflow.domain.usecase.lunge

import com.jeong.fitflow.domain.contract.LUNGE_KNEE_ANGLE_MAX_JUMP
import com.jeong.fitflow.domain.contract.LUNGE_KNEE_ANGLE_MAX_VALID
import com.jeong.fitflow.domain.contract.LUNGE_KNEE_ANGLE_MIN_VALID
import com.jeong.fitflow.domain.model.LungeKneeAngleOutlierReason
import com.jeong.fitflow.domain.model.PoseSide
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LungeKneeAngleSanitizerTest {
    private val sanitizer = LungeKneeAngleSanitizer()

    @Test
    fun sanitizeReturnsNullsWhenRawAngleMissing() {
        val result =
            sanitizer.sanitize(rawAngle = null, lastValidAngle = null, side = PoseSide.LEFT)

        assertNull(result.angle)
        assertNull(result.outlier)
    }

    @Test
    fun sanitizeFlagsLowRangeOutlier() {
        val rawAngle = LUNGE_KNEE_ANGLE_MIN_VALID - 1f

        val result =
            sanitizer.sanitize(rawAngle = rawAngle, lastValidAngle = 60f, side = PoseSide.RIGHT)

        assertNull(result.angle)
        val outlier = result.outlier
        assertTrue(outlier != null)
        assertEquals(PoseSide.RIGHT, outlier?.side)
        assertEquals(LungeKneeAngleOutlierReason.LOW_RANGE, outlier?.reason)
    }

    @Test
    fun sanitizeFlagsHighRangeOutlier() {
        val rawAngle = LUNGE_KNEE_ANGLE_MAX_VALID + 1f

        val result =
            sanitizer.sanitize(rawAngle = rawAngle, lastValidAngle = 120f, side = PoseSide.LEFT)

        assertNull(result.angle)
        val outlier = result.outlier
        assertTrue(outlier != null)
        assertEquals(PoseSide.LEFT, outlier?.side)
        assertEquals(LungeKneeAngleOutlierReason.HIGH_RANGE, outlier?.reason)
    }

    @Test
    fun sanitizeFlagsJumpOutlier() {
        val lastValidAngle = 90f
        val rawAngle = lastValidAngle + LUNGE_KNEE_ANGLE_MAX_JUMP + 1f

        val result = sanitizer.sanitize(
            rawAngle = rawAngle,
            lastValidAngle = lastValidAngle,
            side = PoseSide.RIGHT
        )

        assertNull(result.angle)
        val outlier = result.outlier
        assertTrue(outlier != null)
        assertEquals(PoseSide.RIGHT, outlier?.side)
        assertEquals(LungeKneeAngleOutlierReason.JUMP, outlier?.reason)
    }

    @Test
    fun sanitizeReturnsAngleWhenValid() {
        val lastValidAngle = 100f
        val rawAngle = lastValidAngle + (LUNGE_KNEE_ANGLE_MAX_JUMP / 2f)

        val result = sanitizer.sanitize(
            rawAngle = rawAngle,
            lastValidAngle = lastValidAngle,
            side = PoseSide.LEFT
        )

        assertEquals(rawAngle, result.angle)
        assertNull(result.outlier)
    }
}
