package com.jeong.runninggoaltracker.data.util

object UsernameReservationPolicy {
    fun shouldAllowReservation(
        exists: Boolean,
        ownerUid: String?,
        currentUid: String
    ): Boolean =
        !exists || ownerUid == currentUid
}
