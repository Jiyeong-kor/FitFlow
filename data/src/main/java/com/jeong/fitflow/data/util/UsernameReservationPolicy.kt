package com.jeong.fitflow.data.util

object UsernameReservationPolicy {
    fun shouldAllowReservation(
        isExisting: Boolean,
        ownerUid: String?,
        currentUid: String
    ): Boolean =
        !isExisting || ownerUid == currentUid
}
