package com.jeong.runninggoaltracker.feature.ai_coach.logging

import com.jeong.runninggoaltracker.domain.model.PostureFeedbackType
import com.jeong.runninggoaltracker.domain.model.PostureWarningEvent
import com.jeong.runninggoaltracker.domain.model.SquatFrameMetrics
import com.jeong.runninggoaltracker.domain.model.SquatPhaseTransition
import com.jeong.runninggoaltracker.feature.ai_coach.contract.SmartWorkoutLogContract

class SmartWorkoutLogFormatter {
    fun formatTransition(transition: SquatPhaseTransition, frameMetrics: SquatFrameMetrics): String =
        buildString {
            append(SmartWorkoutLogContract.TRANSITION_PREFIX)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_FROM)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(transition.from.name)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_TO)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(transition.to.name)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_TIMESTAMP)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(transition.timestampMs)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_REASON)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(transition.reason)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_SIDE)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(frameMetrics.side.name)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_PHASE)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(frameMetrics.phase.name)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_KNEE)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(frameMetrics.kneeAngleEma)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_TRUNK_TILT)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(frameMetrics.trunkTiltVerticalAngleEma)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_TRUNK_TO_THIGH)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(frameMetrics.trunkToThighAngleEma)
        }

    fun formatRepCountUpdate(source: String, repCount: Int, timestampMs: Long): String =
        buildString {
            append(SmartWorkoutLogContract.EVENT_REP_COUNT)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_SOURCE)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(source)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_TIMESTAMP)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(timestampMs)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_REP_COUNT)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(repCount)
        }

    fun formatWarningEvent(event: PostureWarningEvent): String =
        buildString {
            append(SmartWorkoutLogContract.EVENT_WARNING)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_FEEDBACK)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(event.feedbackType.name)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_METRIC)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(event.metric.name)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_VALUE)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(event.value)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_THRESHOLD)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(event.threshold)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_OPERATOR)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(event.operator.name)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_STATE)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(event.phase.name)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_TIMESTAMP)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(event.timestampMs)
        }

    fun formatAttemptStart(metrics: SquatFrameMetrics, timestampMs: Long): String =
        buildString {
            append(SmartWorkoutLogContract.EVENT_ATTEMPT_START)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_TIMESTAMP)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(timestampMs)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_ATTEMPT_ACTIVE)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(metrics.isAttemptActive)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_KNEE_MIN)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(metrics.attemptMinKneeAngle)
        }

    fun formatAttemptEnd(metrics: SquatFrameMetrics, timestampMs: Long): String =
        buildString {
            append(SmartWorkoutLogContract.EVENT_ATTEMPT_END)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_TIMESTAMP)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(timestampMs)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_ATTEMPT_ACTIVE)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(metrics.isAttemptActive)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_DEPTH_REACHED)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(metrics.isDepthReached)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_KNEE_MIN)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(metrics.attemptMinKneeAngle)
        }

    fun formatDepthReached(metrics: SquatFrameMetrics, timestampMs: Long): String =
        buildString {
            append(SmartWorkoutLogContract.EVENT_DEPTH_REACHED)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_TIMESTAMP)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(timestampMs)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_DEPTH_REACHED)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(metrics.isDepthReached)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_KNEE_MIN)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(metrics.attemptMinKneeAngle)
        }

    fun formatFullBodyVisibility(metrics: SquatFrameMetrics, timestampMs: Long): String =
        buildString {
            append(SmartWorkoutLogContract.EVENT_FULL_BODY_VISIBILITY)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_TIMESTAMP)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(timestampMs)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_FULL_BODY_VISIBLE)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(metrics.isFullBodyVisible)
            append(SmartWorkoutLogContract.LOG_SEPARATOR)
            append(SmartWorkoutLogContract.KEY_INVISIBLE_DURATION)
            append(SmartWorkoutLogContract.LOG_ASSIGN)
            append(metrics.fullBodyInvisibleDurationMs)
        }

    fun formatFeedbackEvent(
        feedbackType: PostureFeedbackType,
        feedbackKey: String,
        timestampMs: Long
    ): String = buildString {
        append(SmartWorkoutLogContract.EVENT_FEEDBACK_EMIT)
        append(SmartWorkoutLogContract.LOG_SEPARATOR)
        append(SmartWorkoutLogContract.KEY_FEEDBACK)
        append(SmartWorkoutLogContract.LOG_ASSIGN)
        append(feedbackType.name)
        append(SmartWorkoutLogContract.LOG_SEPARATOR)
        append(SmartWorkoutLogContract.KEY_VALUE)
        append(SmartWorkoutLogContract.LOG_ASSIGN)
        append(feedbackKey)
        append(SmartWorkoutLogContract.LOG_SEPARATOR)
        append(SmartWorkoutLogContract.KEY_TIMESTAMP)
        append(SmartWorkoutLogContract.LOG_ASSIGN)
        append(timestampMs)
    }

    fun formatSkippedFrame(timestampMs: Long): String = buildString {
        append(SmartWorkoutLogContract.EVENT_FRAME_SKIP)
        append(SmartWorkoutLogContract.LOG_SEPARATOR)
        append(SmartWorkoutLogContract.KEY_TIMESTAMP)
        append(SmartWorkoutLogContract.LOG_ASSIGN)
        append(timestampMs)
    }
}
