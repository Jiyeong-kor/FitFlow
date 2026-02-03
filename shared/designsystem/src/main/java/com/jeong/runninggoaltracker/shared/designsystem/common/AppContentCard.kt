package com.jeong.runninggoaltracker.shared.designsystem.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppShapes

@Composable
fun AppContentCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentPadding: PaddingValues? = null,
    content: @Composable () -> Unit
) {
    val dimensions = LocalAppDimensions.current
    val appShapes = LocalAppShapes.current
    val resolvedContentPadding = contentPadding ?: PaddingValues(dimensions.cardPaddingLarge)
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = appShapes.roundedXl,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(resolvedContentPadding)
        ) {
            content()
        }
    }
}
