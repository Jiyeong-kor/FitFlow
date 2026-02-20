package com.jeong.fitflow.feature.ai_coach.logging

import com.jeong.fitflow.domain.model.PostureFeedbackType
import com.jeong.fitflow.domain.model.PostureWarningEvent
import com.jeong.fitflow.domain.model.SquatFrameMetrics
import com.jeong.fitflow.domain.model.SquatPhaseTransition
import javax.inject.Inject

class SmartWorkoutAnalyticsLogger @Inject constructor(
    private val formatter: SmartWorkoutLogFormatter,
    private val smartWorkoutLogger: SmartWorkoutLogger
) : WorkoutAnalyticsLogger {
    override fun logTransition(transition: SquatPhaseTransition, frameMetrics: SquatFrameMetrics) {
        smartWorkoutLogger.logDebug {
            formatter.formatTransition(transition, frameMetrics)
        }
    }

    override fun logRepCountUpdate(source: String, repCount: Int, timestampMs: Long) {
        smartWorkoutLogger.logDebug {
            formatter.formatRepCountUpdate(source, repCount, timestampMs)
        }
    }

    override fun logWarningEvent(event: PostureWarningEvent) {
        smartWorkoutLogger.logDebug {
            formatter.formatWarningEvent(event)
        }
    }

    override fun logAttemptStart(metrics: SquatFrameMetrics, timestampMs: Long) {
        smartWorkoutLogger.logDebug {
            formatter.formatAttemptStart(metrics, timestampMs)
        }
    }

    override fun logAttemptEnd(metrics: SquatFrameMetrics, timestampMs: Long) {
        smartWorkoutLogger.logDebug {
            formatter.formatAttemptEnd(metrics, timestampMs)
        }
    }

    override fun logDepthReached(metrics: SquatFrameMetrics, timestampMs: Long) {
        smartWorkoutLogger.logDebug {
            formatter.formatDepthReached(metrics, timestampMs)
        }
    }

    override fun logFullBodyVisibility(metrics: SquatFrameMetrics, timestampMs: Long) {
        smartWorkoutLogger.logDebug {
            formatter.formatFullBodyVisibility(metrics, timestampMs)
        }
    }

    override fun logFeedbackEvent(
        feedbackType: PostureFeedbackType,
        feedbackKey: String,
        timestampMs: Long
    ) {
        smartWorkoutLogger.logDebug {
            formatter.formatFeedbackEvent(feedbackType, feedbackKey, timestampMs)
        }
    }

    override fun logSkippedFrame(timestampMs: Long) {
        smartWorkoutLogger.logDebug {
            formatter.formatSkippedFrame(timestampMs)
        }
    }
}
