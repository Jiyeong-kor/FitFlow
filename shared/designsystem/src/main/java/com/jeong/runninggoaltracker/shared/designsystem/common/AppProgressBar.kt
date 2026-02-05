package com.jeong.runninggoaltracker.shared.designsystem.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppAlphas
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions
import com.jeong.runninggoaltracker.shared.designsystem.theme.appProgressGradient

@Composable
fun AppProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalAppDimensions.current
    val resolvedHeight = dimensions.progressBarHeight
    val alphas = LocalAppAlphas.current
    val backgroundAlpha = alphas.progressBarBackground
    val resolvedBackgroundColor =
        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = backgroundAlpha)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(resolvedHeight)
            .clip(CircleShape)
            .background(resolvedBackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(appProgressGradient())
        )
    }
}
