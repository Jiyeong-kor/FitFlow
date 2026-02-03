package com.jeong.runninggoaltracker.shared.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

data class AppShapes(
    val roundedXs: Shape,
    val roundedSm: Shape,
    val roundedMd: Shape,
    val roundedLg: Shape,
    val roundedXl: Shape,
    val rounded2xl: Shape
)

val LocalAppShapes = staticCompositionLocalOf {
    AppShapes(
        roundedXs = RoundedCornerShape(0.dp),
        roundedSm = RoundedCornerShape(0.dp),
        roundedMd = RoundedCornerShape(0.dp),
        roundedLg = RoundedCornerShape(0.dp),
        roundedXl = RoundedCornerShape(0.dp),
        rounded2xl = RoundedCornerShape(0.dp)
    )
}

fun appShapes(): AppShapes = AppShapes(
    roundedXs = RoundedCornerShape(8.dp),
    roundedSm = RoundedCornerShape(12.dp),
    roundedMd = RoundedCornerShape(16.dp),
    roundedLg = RoundedCornerShape(20.dp),
    roundedXl = RoundedCornerShape(24.dp),
    rounded2xl = RoundedCornerShape(28.dp)
)
