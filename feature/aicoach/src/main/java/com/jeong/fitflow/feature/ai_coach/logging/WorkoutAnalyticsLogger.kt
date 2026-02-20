package com.jeong.fitflow.feature.ai_coach.logging

import com.jeong.fitflow.domain.model.PostureFeedbackType
import com.jeong.fitflow.domain.model.PostureWarningEvent
import com.jeong.fitflow.domain.model.SquatFrameMetrics
import com.jeong.fitflow.domain.model.SquatPhaseTransition

interface WorkoutAnalyticsLogger {
    fun logTransition(transition: SquatPhaseTransition, frameMetrics: SquatFrameMetrics)
    fun logRepCountUpdate(source: String, repCount: Int, timestampMs: Long)
    fun logWarningEvent(event: PostureWarningEvent)
    fun logAttemptStart(metrics: SquatFrameMetrics, timestampMs: Long)
    fun logAttemptEnd(metrics: SquatFrameMetrics, timestampMs: Long)
    fun logDepthReached(metrics: SquatFrameMetrics, timestampMs: Long)
    fun logFullBodyVisibility(metrics: SquatFrameMetrics, timestampMs: Long)
    fun logFeedbackEvent(feedbackType: PostureFeedbackType, feedbackKey: String, timestampMs: Long)
    fun logSkippedFrame(timestampMs: Long)
}
