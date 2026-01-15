package com.jeong.runninggoaltracker.domain.model

data class PoseLandmark(
    val type: PoseLandmarkType,
    val x: Float,
    val y: Float,
    val z: Float,
    val confidence: Float
)
