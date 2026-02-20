package com.jeong.fitflow.shared.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Brush

@Composable
fun appAccentColor(): Color = MaterialTheme.colorScheme.primary

@Composable
fun appOnAccentColor(): Color = MaterialTheme.colorScheme.onPrimary

@Composable
fun appBackgroundColor(): Color = MaterialTheme.colorScheme.background

@Composable
fun appSurfaceColor(): Color = MaterialTheme.colorScheme.surfaceContainer

@Composable
fun appTextPrimaryColor(): Color = MaterialTheme.colorScheme.onSurface

@Composable
fun appTextMutedColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant


@Composable
fun appBrandGradient(): Brush = Brush.linearGradient(
    colors = listOf(brandGradientStart, brandGradientEnd)
)

@Composable
fun appProgressGradient(): Brush = Brush.horizontalGradient(
    colors = listOf(progressGradientStart, progressGradientEnd)
)

@Composable
fun appDashboardGradient(): Brush = Brush.linearGradient(
    colors = listOf(dashboardGradientStart, dashboardGradientEnd)
)

@Composable
fun appSpacingSm(): Dp = LocalAppDimensions.current.spacingSm

@Composable
fun appSpacingMd(): Dp = LocalAppDimensions.current.spacingMd

@Composable
fun appSpacingLg(): Dp = LocalAppDimensions.current.spacingLg

@Composable
fun appSpacingXl(): Dp = LocalAppDimensions.current.spacingXl

@Composable
fun appSpacing2xl(): Dp = LocalAppDimensions.current.spacing2xl
