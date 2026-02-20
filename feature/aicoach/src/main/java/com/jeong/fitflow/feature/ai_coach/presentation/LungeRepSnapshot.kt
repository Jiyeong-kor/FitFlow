package com.jeong.fitflow.feature.ai_coach.presentation

import com.jeong.fitflow.domain.model.PoseSide
import com.jeong.fitflow.domain.model.PostureFeedbackType

data class LungeRepSnapshot(
    val timestampMs: Long,
    val activeSide: PoseSide?,
    val countingSide: PoseSide?,
    val feedbackType: PostureFeedbackType?,
    val feedbackEventKey: String?,
    val feedbackKeys: List<String>,
    val overallScore: Int?,
    val frontKneeMinAngle: Float?,
    val backKneeMinAngle: Float?,
    val maxTorsoLeanAngle: Float?,
    val stabilityStdDev: Float?,
    val maxKneeForwardRatio: Float?,
    val maxKneeCollapseRatio: Float?,
    val goodFormReason: LungeGoodFormReason
)
