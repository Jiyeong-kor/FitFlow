package com.jeong.runninggoaltracker.domain.model

data class PoseAnalysisResult(
    val repCount: RepCount,
    val feedback: PostureFeedback,
    val feedbackEvent: PostureFeedbackType?,
    val feedbackEventKey: String? = null,
    val frameMetrics: SquatFrameMetrics?,
    val repSummary: SquatRepSummary?,
    val lungeRepSummary: LungeRepSummary?,
    val warningEvent: PostureWarningEvent?,
    val feedbackKeys: List<String>,
    val isLowConfidenceSkipped: Boolean,
    val lungeDebugInfo: LungeDebugInfo? = null
)
