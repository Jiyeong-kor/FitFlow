package com.jeong.runninggoaltracker.shared.designsystem.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppShapes
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppAlphas

enum class AppSurfaceCardTone {
    Default,
    Emphasized
}

enum class AppSurfaceCardPadding {
    Default,
    Large,
    Wide
}

@Composable
fun AppSurfaceCard(
    modifier: Modifier = Modifier,
    tone: AppSurfaceCardTone = AppSurfaceCardTone.Default,
    padding: AppSurfaceCardPadding = AppSurfaceCardPadding.Default,
    isEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val dimensions = LocalAppDimensions.current
    val alphas = LocalAppAlphas.current
    val appShapes = LocalAppShapes.current
    val resolvedShape = appShapes.roundedXl
    val baseContainerColor = when (tone) {
        AppSurfaceCardTone.Default -> MaterialTheme.colorScheme.surfaceContainer
        AppSurfaceCardTone.Emphasized -> MaterialTheme.colorScheme.primaryContainer
    }
    val resolvedContainerColor = if (isEnabled) {
        baseContainerColor
    } else {
        baseContainerColor.copy(alpha = alphas.reminderDisabledSurface)
    }
    val containerColor by animateColorAsState(
        targetValue = resolvedContainerColor,
        label = "AppSurfaceCardContainer"
    )
    val resolvedContentPadding = when (padding) {
        AppSurfaceCardPadding.Default -> PaddingValues(dimensions.surfaceCardContentPadding)
        AppSurfaceCardPadding.Large -> PaddingValues(dimensions.spacingLg)
        AppSurfaceCardPadding.Wide -> PaddingValues(
            horizontal = dimensions.spacingXl,
            vertical = dimensions.spacingLg
        )
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = resolvedShape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensions.sizeZero
        )
    ) {
        Box(modifier = Modifier.padding(resolvedContentPadding)) {
            content()
        }
    }
}
