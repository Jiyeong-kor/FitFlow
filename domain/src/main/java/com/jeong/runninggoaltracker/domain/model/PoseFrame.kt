package com.jeong.runninggoaltracker.domain.model

data class PoseFrame(
    val landmarks: List<PoseLandmark>
) {
    fun landmark(type: PoseLandmarkType): PoseLandmark? = landmarks.firstOrNull { it.type == type }
}
