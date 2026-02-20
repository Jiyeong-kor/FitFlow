package com.jeong.fitflow.feature.ai_coach.data.pose

import androidx.camera.core.ImageAnalysis
import com.jeong.fitflow.domain.model.PoseFrame
import kotlinx.coroutines.flow.Flow

interface PoseDetector {
    val poseFrames: Flow<PoseFrame>
    val imageAnalyzer: ImageAnalysis.Analyzer
    fun clear()
}
