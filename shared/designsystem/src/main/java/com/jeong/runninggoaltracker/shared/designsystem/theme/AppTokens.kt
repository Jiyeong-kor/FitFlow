package com.jeong.runninggoaltracker.shared.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.jeong.runninggoaltracker.shared.designsystem.R
import androidx.compose.ui.graphics.Color

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
fun appSpacingSm(): Dp = dimensionResource(R.dimen.spacing_sm)

@Composable
fun appSpacingMd(): Dp = dimensionResource(R.dimen.spacing_md)

@Composable
fun appSpacingLg(): Dp = dimensionResource(R.dimen.spacing_lg)

@Composable
fun appSpacingXl(): Dp = dimensionResource(R.dimen.spacing_xl)

@Composable
fun appSpacing2xl(): Dp = dimensionResource(R.dimen.spacing_2xl)
