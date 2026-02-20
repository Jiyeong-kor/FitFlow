package com.jeong.fitflow.domain.model

data class PoseCalibration(
    val baselineKneeAngle: Float,
    val baselineTrunkTiltVerticalAngle: Float,
    val baselineLegLength: Float,
    val baselineAnkleY: Float,
    val baselineKneeX: Float
)
