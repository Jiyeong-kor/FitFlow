package com.jeong.runninggoaltracker.shared.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.jeong.runninggoaltracker.shared.designsystem.R

data class AppDimensions(
    val spacingXs: Dp,
    val spacingSm: Dp,
    val spacingMd: Dp,
    val spacingLg: Dp,
    val spacingXl: Dp,
    val spacing2xl: Dp,
    val touchTargetMin: Dp,
    val iconMd: Dp,
    val cardCornerRadius: Dp,
    val cardPaddingLarge: Dp,
    val surfaceCardContentPadding: Dp,
    val cardSpacingSmall: Dp,
    val cardElevation: Dp,
    val progressBarHeight: Dp,
    val sizeZero: Dp
)

val LocalAppDimensions = staticCompositionLocalOf {
    AppDimensions(
        spacingXs = Dp.Unspecified,
        spacingSm = Dp.Unspecified,
        spacingMd = Dp.Unspecified,
        spacingLg = Dp.Unspecified,
        spacingXl = Dp.Unspecified,
        spacing2xl = Dp.Unspecified,
        touchTargetMin = Dp.Unspecified,
        iconMd = Dp.Unspecified,
        cardCornerRadius = Dp.Unspecified,
        cardPaddingLarge = Dp.Unspecified,
        surfaceCardContentPadding = Dp.Unspecified,
        cardSpacingSmall = Dp.Unspecified,
        cardElevation = Dp.Unspecified,
        progressBarHeight = Dp.Unspecified,
        sizeZero = Dp.Unspecified
    )
}

@Composable
fun appDimensions(): AppDimensions = AppDimensions(
    spacingXs = dimensionResource(R.dimen.spacing_xs),
    spacingSm = dimensionResource(R.dimen.spacing_sm),
    spacingMd = dimensionResource(R.dimen.spacing_md),
    spacingLg = dimensionResource(R.dimen.spacing_lg),
    spacingXl = dimensionResource(R.dimen.spacing_xl),
    spacing2xl = dimensionResource(R.dimen.spacing_2xl),
    touchTargetMin = dimensionResource(R.dimen.touch_target_min),
    iconMd = dimensionResource(R.dimen.icon_md),
    cardCornerRadius = dimensionResource(R.dimen.card_corner_radius),
    cardPaddingLarge = dimensionResource(R.dimen.card_padding_large),
    surfaceCardContentPadding = dimensionResource(R.dimen.app_surface_card_content_padding),
    cardSpacingSmall = dimensionResource(R.dimen.card_spacing_small),
    cardElevation = dimensionResource(R.dimen.card_elevation),
    progressBarHeight = dimensionResource(R.dimen.progress_bar_height),
    sizeZero = dimensionResource(R.dimen.size_zero)
)
