package com.jeong.runninggoaltracker.domain.model

data class LungeDebugInfo(
    val activeSide: PoseSide,
    val countingSide: PoseSide?,
    val leftKneeAngleRaw: Float?,
    val rightKneeAngleRaw: Float?,
    val leftKneeAngleSanitized: Float?,
    val rightKneeAngleSanitized: Float?,
    val leftOutlierReason: LungeKneeAngleOutlierReason?,
    val rightOutlierReason: LungeKneeAngleOutlierReason?,
    val repMinUpdated: Boolean,
    val hipSampleCount: Int,
    val shoulderSampleCount: Int,
    val hipCenterX: Float?,
    val shoulderCenterX: Float?,
    val hipCenterMin: Float?,
    val hipCenterMax: Float?,
    val shoulderCenterMin: Float?,
    val shoulderCenterMax: Float?,
    val stabilityStdDev: Float,
    val stabilityEligible: Boolean,
    val stabilityNormalized: Boolean,
    val feedbackEventKey: String?
)
