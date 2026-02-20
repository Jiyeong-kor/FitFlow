package com.jeong.fitflow.feature.ai_coach.logging

import com.jeong.fitflow.domain.model.PostureFeedbackType
import com.jeong.fitflow.domain.model.PostureWarningEvent
import com.jeong.fitflow.domain.model.SquatFrameMetrics
import com.jeong.fitflow.domain.model.SquatPhaseTransition
import javax.inject.Inject

class SmartWorkoutAnalyticsLogger @Inject constructor(
    private val formatter: SmartWorkoutLogFormatter
) : WorkoutAnalyticsLogger {
    override fun logTransition(transition: SquatPhaseTransition, frameMetrics: SquatFrameMetrics) {
        SmartWorkoutLogger.logDebug {
            formatter.formatTransition(transition, frameMetrics)
        }
    }

    override fun logRepCountUpdate(source: String, repCount: Int, timestampMs: Long) {
        SmartWorkoutLogger.logDebug {
            formatter.formatRepCountUpdate(source, repCount, timestampMs)
        }
    }

    override fun logWarningEvent(event: PostureWarningEvent) {
        SmartWorkoutLogger.logDebug {
            formatter.formatWarningEvent(event)
        }
    }

    override fun logAttemptStart(metrics: SquatFrameMetrics, timestampMs: Long) {
        SmartWorkoutLogger.logDebug {
            formatter.formatAttemptStart(metrics, timestampMs)
        }
    }

    override fun logAttemptEnd(metrics: SquatFrameMetrics, timestampMs: Long) {
        SmartWorkoutLogger.logDebug {
            formatter.formatAttemptEnd(metrics, timestampMs)
        }
    }

    override fun logDepthReached(metrics: SquatFrameMetrics, timestampMs: Long) {
        SmartWorkoutLogger.logDebug {
            formatter.formatDepthReached(metrics, timestampMs)
        }
    }

    override fun logFullBodyVisibility(metrics: SquatFrameMetrics, timestampMs: Long) {
        SmartWorkoutLogger.logDebug {
            formatter.formatFullBodyVisibility(metrics, timestampMs)
        }
    }

    override fun logFeedbackEvent(
        feedbackType: PostureFeedbackType,
        feedbackKey: String,
        timestampMs: Long
    ) {
        SmartWorkoutLogger.logDebug {
            formatter.formatFeedbackEvent(feedbackType, feedbackKey, timestampMs)
        }
    }

    override fun logSkippedFrame(timestampMs: Long) {
        SmartWorkoutLogger.logDebug {
            formatter.formatSkippedFrame(timestampMs)
        }
    }
}
