package com.jeong.runninggoaltracker.data.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UsernameReservationPolicyTest {

    @Test
    fun `reservation allowed when username missing`() {
        val allowed = UsernameReservationPolicy.shouldAllowReservation(
            exists = false,
            ownerUid = null,
            currentUid = "uid-1"
        )

        assertTrue(allowed)
    }

    @Test
    fun `reservation denied when owned by different user`() {
        val allowed = UsernameReservationPolicy.shouldAllowReservation(
            exists = true,
            ownerUid = "uid-2",
            currentUid = "uid-1"
        )

        assertFalse(allowed)
    }
}
