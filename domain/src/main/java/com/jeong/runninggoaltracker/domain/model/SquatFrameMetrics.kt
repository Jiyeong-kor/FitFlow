package com.jeong.runninggoaltracker.domain.model

data class SquatFrameMetrics(
    val kneeAngle: Float,
    val trunkLeanAngle: Float,
    val heelRiseRatio: Float?,
    val kneeForwardRatio: Float?,
    val state: SquatState,
    val isLandmarkReliable: Boolean,
    val isCalibrated: Boolean
)
