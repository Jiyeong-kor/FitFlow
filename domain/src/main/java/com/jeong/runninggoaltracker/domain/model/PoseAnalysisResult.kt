package com.jeong.runninggoaltracker.domain.model

data class PoseAnalysisResult(
    val repCount: RepCount,
    val feedback: PostureFeedback,
    val feedbackEvent: PostureFeedbackType?,
    val frameMetrics: SquatFrameMetrics?,
    val repSummary: SquatRepSummary?,
    val warningEvent: PostureWarningEvent?,
    val skippedLowConfidence: Boolean
)
