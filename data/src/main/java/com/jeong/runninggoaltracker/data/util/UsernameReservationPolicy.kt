package com.jeong.runninggoaltracker.data.util

object UsernameReservationPolicy {
    fun shouldAllowReservation(
        isExisting: Boolean,
        ownerUid: String?,
        currentUid: String
    ): Boolean =
        !isExisting || ownerUid == currentUid
}
