package com.jeong.fitflow.shared.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf


data class AppAlphas(
    val recordAccentWeak: Float,
    val recordAccentStrong: Float,
    val recordMetricBackground: Float,
    val goalBorder: Float,
    val goalSelectedBackground: Float,
    val goalUnselectedBackground: Float,
    val reminderDisabledSurface: Float,
    val reminderSelectedDayBackground: Float,
    val reminderUnselectedDayText: Float,
    val homeProgressIconTint: Float,
    val homeInfoCardBackground: Float,
    val homeActivityRowBackground: Float,
    val homeActivityIconContainer: Float,
    val homeActivityArrowTint: Float,
    val homeSummaryMetricBackground: Float,
    val progressBarBackground: Float,
    val debugOverlaySurface: Float,
    val transparent: Float
)

val LocalAppAlphas = staticCompositionLocalOf {
    AppAlphas(
        recordAccentWeak = 0f,
        recordAccentStrong = 0f,
        recordMetricBackground = 0f,
        goalBorder = 0f,
        goalSelectedBackground = 0f,
        goalUnselectedBackground = 0f,
        reminderDisabledSurface = 0f,
        reminderSelectedDayBackground = 0f,
        reminderUnselectedDayText = 0f,
        homeProgressIconTint = 0f,
        homeInfoCardBackground = 0f,
        homeActivityRowBackground = 0f,
        homeActivityIconContainer = 0f,
        homeActivityArrowTint = 0f,
        homeSummaryMetricBackground = 0f,
        progressBarBackground = 0f,
        debugOverlaySurface = 0f,
        transparent = 0f
    )
}

fun appAlphas(): AppAlphas = AppAlphas(
    recordAccentWeak = 0.05f,
    recordAccentStrong = 0.3f,
    recordMetricBackground = 0.03f,
    goalBorder = 0.1f,
    goalSelectedBackground = 0.1f,
    goalUnselectedBackground = 0.03f,
    reminderDisabledSurface = 0.02f,
    reminderSelectedDayBackground = 0.2f,
    reminderUnselectedDayText = 0.5f,
    homeProgressIconTint = 0.4f,
    homeInfoCardBackground = 0.03f,
    homeActivityRowBackground = 0.02f,
    homeActivityIconContainer = 0.05f,
    homeActivityArrowTint = 0.4f,
    homeSummaryMetricBackground = 0.1f,
    progressBarBackground = 0.7f,
    debugOverlaySurface = 0.92f,
    transparent = 0f
)
