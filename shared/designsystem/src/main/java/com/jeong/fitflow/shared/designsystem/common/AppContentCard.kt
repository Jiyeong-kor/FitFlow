package com.jeong.fitflow.shared.designsystem.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jeong.fitflow.shared.designsystem.theme.LocalAppDimensions
import com.jeong.fitflow.shared.designsystem.theme.LocalAppShapes

@Composable
fun AppContentCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val dimensions = LocalAppDimensions.current
    val appShapes = LocalAppShapes.current
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = appShapes.roundedXl,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(PaddingValues(dimensions.cardPaddingLarge))
        ) {
            content()
        }
    }
}
