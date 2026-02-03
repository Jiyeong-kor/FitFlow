package com.jeong.runninggoaltracker.shared.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


data class AppTypographyTokens(
    val displayHuge: TextStyle,
    val displayLarge: TextStyle,
    val headlineLarge: TextStyle,
    val titleLargeAlt: TextStyle,
    val labelTiny: TextStyle,
    val recordLabel: TextStyle
)

val LocalAppTypographyTokens = staticCompositionLocalOf {
    AppTypographyTokens(
        displayHuge = TextStyle.Default,
        displayLarge = TextStyle.Default,
        headlineLarge = TextStyle.Default,
        titleLargeAlt = TextStyle.Default,
        labelTiny = TextStyle.Default,
        recordLabel = TextStyle.Default
    )
}

fun appTypographyTokens(): AppTypographyTokens = AppTypographyTokens(
    displayHuge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 160.sp,
        lineHeight = 160.sp
    ),
    displayLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
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
