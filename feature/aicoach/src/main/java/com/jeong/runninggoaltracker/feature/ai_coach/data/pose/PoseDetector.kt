package com.jeong.runninggoaltracker.feature.ai_coach.data.pose

import androidx.camera.core.ImageAnalysis
import com.jeong.runninggoaltracker.domain.model.PoseFrame
import kotlinx.coroutines.flow.Flow

interface PoseDetector {
    val poseFrames: Flow<PoseFrame>
    val imageAnalyzer: ImageAnalysis.Analyzer
    fun clear()
}
