package com.jeong.runninggoaltracker.shared.designsystem.theme

import androidx.annotation.DimenRes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.jeong.runninggoaltracker.shared.designsystem.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_thin, FontWeight.Thin),
    Font(R.font.pretendard_extralight, FontWeight.ExtraLight),
    Font(R.font.pretendard_light, FontWeight.Light),
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_extrabold, FontWeight.ExtraBold),
    Font(R.font.pretendard_black, FontWeight.Black)
)

@Composable
fun appTypography(): Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = textSize(R.dimen.text_size_title_large),
        lineHeight = textSize(R.dimen.line_height_title_large)
    ),
    titleMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = textSize(R.dimen.text_size_title_medium),
        lineHeight = textSize(R.dimen.line_height_title_medium)
    ),
    bodyLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = textSize(R.dimen.text_size_body_large),
        lineHeight = textSize(R.dimen.line_height_body_large)
    ),
    bodyMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Light,
        fontSize = textSize(R.dimen.text_size_body_medium),
        lineHeight = textSize(R.dimen.line_height_body_medium)
    ),
    labelSmall = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = textSize(R.dimen.text_size_label_small),
        lineHeight = textSize(R.dimen.line_height_label_small)
    )
)

@Composable
private fun textSize(@DimenRes resId: Int): TextUnit =
    with(LocalDensity.current) { dimensionResource(id = resId).toSp() }
