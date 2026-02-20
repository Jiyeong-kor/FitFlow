package com.jeong.fitflow.feature.ai_coach.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.jeong.fitflow.domain.model.ExerciseType
import com.jeong.fitflow.domain.model.LungeDebugInfo
import com.jeong.fitflow.domain.model.LungeKneeAngleOutlierReason
import com.jeong.fitflow.domain.model.PoseSide
import com.jeong.fitflow.domain.model.PostureFeedbackType
import com.jeong.fitflow.domain.model.SquatFrameMetrics
import com.jeong.fitflow.feature.ai_coach.R
import com.jeong.fitflow.shared.designsystem.theme.LocalAppAlphas
import com.jeong.fitflow.shared.designsystem.theme.LocalAppDimensions
import com.jeong.fitflow.shared.designsystem.theme.LocalAppShapes
import com.jeong.fitflow.shared.designsystem.theme.LocalAppTypographyTokens
import com.jeong.fitflow.shared.designsystem.theme.appTextMutedColor
import com.jeong.fitflow.shared.designsystem.theme.appTextPrimaryColor

@Composable
fun LungeDebugOverlay(
    debugInfo: LungeDebugInfo?,
    snapshot: LungeRepSnapshot?,
    frameMetrics: SquatFrameMetrics?,
    modifier: Modifier = Modifier
) {
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val dimensions = LocalAppDimensions.current
    val appShapes = LocalAppShapes.current
    val typographyTokens = LocalAppTypographyTokens.current
    val alphas = LocalAppAlphas.current
    val emptyText = stringResource(R.string.smart_workout_debug_lunge_empty)

    Card(
        modifier = modifier,
        shape = appShapes.roundedXl,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = alphas.debugOverlaySurface)
        )
    ) {
        Box(modifier = Modifier.padding(PaddingValues(dimensions.spacingMd))) {
            Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacingSm)) {
                Text(
                    text = stringResource(R.string.smart_workout_debug_lunge_title),
                    color = textPrimary,
                    style = typographyTokens.titleLargeAlt
                )

                frameMetrics?.let { metrics ->
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_live_knee_raw,
                            metrics.kneeAngleRaw
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_live_knee_ema,
                            metrics.kneeAngleEma
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_live_trunk_raw,
                            metrics.trunkTiltVerticalAngleRaw
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_live_trunk_ema,
                            metrics.trunkTiltVerticalAngleEma
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_rep_min_knee_tracking,
                            metrics.repMinKneeAngle
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_rep_trunk_max_tracking,
                            metrics.repMaxTrunkTiltVerticalAngle
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                }

                debugInfo?.let { info ->
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_active_side,
                            poseSideText(info.activeSide)
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_state,
                            info.state.name
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_phase,
                            info.phase.name
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_reliable,
                            booleanText(info.isReliable)
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_counting_side,
                            poseSideText(info.countingSide)
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_last_knee_angles,
                            info.lastLeftKneeAngle ?: 0f,
                            info.lastRightKneeAngle ?: 0f
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_feedback_key,
                            info.feedbackEventKey?.let { lungeFeedbackLabel(it) } ?: emptyText
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_stability_eligible,
                            booleanText(info.isStabilityEligible)
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_hip_samples,
                            info.hipSampleCount
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_shoulder_samples,
                            info.shoulderSampleCount
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_metrics_null_rate,
                            info.metricsNullRate,
                            info.metricsNullStreak
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_thresholds,
                            info.standingThreshold,
                            info.descendingThreshold,
                            info.bottomThreshold,
                            info.ascendingThreshold,
                            info.repCompleteThreshold
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_hysteresis_frames,
                            info.hysteresisFrames
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_hysteresis_counts,
                            info.standingToDescendingCount,
                            info.descendingToBottomCount,
                            info.descendingToStandingCount,
                            info.bottomToAscendingCount,
                            info.ascendingToCompleteCount,
                            info.repCompleteToStandingCount,
                            info.repCompleteToDescendingCount
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    info.hipCenterX?.let { hipCenterX ->
                        val hipMin = info.hipCenterMin ?: hipCenterX
                        val hipMax = info.hipCenterMax ?: hipCenterX
                        Text(
                            text = stringResource(
                                R.string.smart_workout_debug_lunge_hip_center,
                                hipCenterX,
                                hipMin,
                                hipMax
                            ),
                            color = textMuted,
                            style = typographyTokens.labelTiny
                        )
                    }
                    info.shoulderCenterX?.let { shoulderCenterX ->
                        val shoulderMin = info.shoulderCenterMin ?: shoulderCenterX
                        val shoulderMax = info.shoulderCenterMax ?: shoulderCenterX
                        Text(
                            text = stringResource(
                                R.string.smart_workout_debug_lunge_shoulder_center,
                                shoulderCenterX,
                                shoulderMin,
                                shoulderMax
                            ),
                            color = textMuted,
                            style = typographyTokens.labelTiny
                        )
                    }
                    if (!info.isStabilityNormalized) {
                        Text(
                            text = stringResource(R.string.smart_workout_debug_lunge_normalization_warning),
                            color = MaterialTheme.colorScheme.error,
                            style = typographyTokens.labelTiny
                        )
                    }
                    val outlierLabels = mutableListOf<String>()
                    info.leftOutlierReason?.let { reason ->
                        outlierLabels += stringResource(
                            R.string.smart_workout_debug_lunge_outlier_left,
                            outlierReasonText(reason)
                        )
                    }
                    info.rightOutlierReason?.let { reason ->
                        outlierLabels += stringResource(
                            R.string.smart_workout_debug_lunge_outlier_right,
                            outlierReasonText(reason)
                        )
                    }
                    val outlierText = if (outlierLabels.isEmpty()) {
                        emptyText
                    } else {
                        outlierLabels.joinToString(
                            separator = stringResource(
                                R.string.smart_workout_debug_lunge_outlier_separator
                            )
                        )
                    }
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_outlier_reason,
                            outlierText
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_rep_min_update,
                            booleanText(info.isRepMinUpdated)
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                }

                Text(
                    text = stringResource(R.string.smart_workout_debug_lunge_last_rep_title),
                    color = textPrimary,
                    style = typographyTokens.titleLargeAlt,
                    fontWeight = FontWeight.SemiBold
                )
                if (snapshot == null) {
                    Text(
                        text = emptyText,
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                } else {
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_rep_timestamp,
                            snapshot.timestampMs
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_active_side,
                            poseSideText(snapshot.activeSide)
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_counting_side,
                            poseSideText(snapshot.countingSide)
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_feedback_event,
                            snapshot.feedbackType?.name ?: emptyText
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_feedback_key,
                            snapshot.feedbackEventKey?.let { lungeFeedbackLabel(it) } ?: emptyText
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    val feedbackLabels = mutableListOf<String>()
                    snapshot.feedbackKeys.forEach { key ->
                        feedbackLabels += lungeFeedbackLabel(key)
                    }
                    val feedbackKeysText = if (feedbackLabels.isEmpty()) {
                        emptyText
                    } else {
                        feedbackLabels.joinToString(
                            separator = stringResource(
                                R.string.smart_workout_debug_lunge_key_separator
                            )
                        )
                    }
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_feedback_keys,
                            feedbackKeysText
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                    snapshot.overallScore?.let { score ->
                        Text(
                            text = stringResource(
                                R.string.smart_workout_debug_lunge_overall_score,
                                score
                            ),
                            color = textMuted,
                            style = typographyTokens.labelTiny
                        )
                    }
                    snapshot.frontKneeMinAngle?.let { angle ->
                        Text(
                            text = stringResource(
                                R.string.smart_workout_debug_lunge_front_knee_min,
                                angle
                            ),
                            color = textMuted,
                            style = typographyTokens.labelTiny
                        )
                    }
                    snapshot.backKneeMinAngle?.let { angle ->
                        Text(
                            text = stringResource(
                                R.string.smart_workout_debug_lunge_back_knee_min,
                                angle
                            ),
                            color = textMuted,
                            style = typographyTokens.labelTiny
                        )
                    }
                    snapshot.maxTorsoLeanAngle?.let { angle ->
                        Text(
                            text = stringResource(
                                R.string.smart_workout_debug_lunge_max_torso_lean,
                                angle
                            ),
                            color = textMuted,
                            style = typographyTokens.labelTiny
                        )
                    }
                    snapshot.stabilityStdDev?.let { value ->
                        Text(
                            text = stringResource(
                                R.string.smart_workout_debug_lunge_stability_stddev,
                                value
                            ),
                            color = textMuted,
                            style = typographyTokens.labelTiny
                        )
                    }
                    snapshot.maxKneeForwardRatio?.let { ratio ->
                        Text(
                            text = stringResource(
                                R.string.smart_workout_debug_lunge_knee_forward_max,
                                ratio
                            ),
                            color = textMuted,
                            style = typographyTokens.labelTiny
                        )
                    }
                    snapshot.maxKneeCollapseRatio?.let { ratio ->
                        Text(
                            text = stringResource(
                                R.string.smart_workout_debug_lunge_knee_collapse_max,
                                ratio
                            ),
                            color = textMuted,
                            style = typographyTokens.labelTiny
                        )
                    }
                    Text(
                        text = stringResource(
                            R.string.smart_workout_debug_lunge_good_form_reason,
                            goodFormReasonText(snapshot.goodFormReason)
                        ),
                        color = textMuted,
                        style = typographyTokens.labelTiny
                    )
                }
            }
        }
    }
}

@Composable
private fun poseSideText(side: PoseSide?): String = when (side) {
    PoseSide.LEFT -> stringResource(R.string.smart_workout_debug_lunge_side_left)
    PoseSide.RIGHT -> stringResource(R.string.smart_workout_debug_lunge_side_right)
    null -> stringResource(R.string.smart_workout_debug_lunge_side_unknown)
}

@Composable
private fun booleanText(isEnabled: Boolean): String =
    if (isEnabled) {
        stringResource(R.string.smart_workout_debug_on)
    } else {
        stringResource(R.string.smart_workout_debug_off)
    }

@Composable
private fun outlierReasonText(reason: LungeKneeAngleOutlierReason): String = when (reason) {
    LungeKneeAngleOutlierReason.LOW_RANGE -> stringResource(R.string.smart_workout_debug_lunge_outlier_low)
    LungeKneeAngleOutlierReason.HIGH_RANGE -> stringResource(R.string.smart_workout_debug_lunge_outlier_high)
    LungeKneeAngleOutlierReason.JUMP -> stringResource(R.string.smart_workout_debug_lunge_outlier_jump)
}

@Composable
private fun goodFormReasonText(reason: LungeGoodFormReason): String = when (reason) {
    LungeGoodFormReason.NO_SUMMARY -> stringResource(
        R.string.smart_workout_debug_lunge_good_form_reason_no_summary
    )

    LungeGoodFormReason.FEEDBACK_KEYS_PRESENT -> stringResource(
        R.string.smart_workout_debug_lunge_good_form_reason_keys
    )

    LungeGoodFormReason.GOOD_FORM -> stringResource(
        R.string.smart_workout_debug_lunge_good_form_reason_good
    )
}

@Composable
private fun lungeFeedbackLabel(key: String): String {
    val feedbackLabel = stringResource(
        FeedbackStringMapper.feedbackResId(
            exerciseType = ExerciseType.LUNGE,
            feedbackType = PostureFeedbackType.UNKNOWN,
            feedbackKey = key
        )
    )
    return stringResource(
        R.string.smart_workout_debug_lunge_feedback_label,
        key,
        feedbackLabel
    )
}
