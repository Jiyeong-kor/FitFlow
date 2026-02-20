package com.jeong.fitflow.shared.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


data class AppTypographyTokens(
    val displayHuge: TextStyle,
    val displayLarge: TextStyle,
    val headlineLarge: TextStyle,
    val titleLargeAlt: TextStyle,
    val numericTitleLarge: TextStyle,
    val numericTitleMedium: TextStyle,
    val numericBodyMedium: TextStyle,
    val labelTiny: TextStyle,
    val recordLabel: TextStyle
)

val LocalAppTypographyTokens = staticCompositionLocalOf {
    AppTypographyTokens(
        displayHuge = TextStyle.Default,
        displayLarge = TextStyle.Default,
        headlineLarge = TextStyle.Default,
        titleLargeAlt = TextStyle.Default,
        numericTitleLarge = TextStyle.Default,
        numericTitleMedium = TextStyle.Default,
        numericBodyMedium = TextStyle.Default,
        labelTiny = TextStyle.Default,
        recordLabel = TextStyle.Default
    )
}

fun appTypographyTokens(): AppTypographyTokens = AppTypographyTokens(
    displayHuge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 160.sp,
        lineHeight = 160.sp
    ),
    displayLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 64.sp,
        lineHeight = 64.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp
    ),
    titleLargeAlt = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    numericTitleLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    numericTitleMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    numericBodyMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelTiny = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    ),
    recordLabel = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 2.sp
    )
)
