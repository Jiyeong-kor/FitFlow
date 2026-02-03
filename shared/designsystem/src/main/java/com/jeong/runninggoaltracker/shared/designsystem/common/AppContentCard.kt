package com.jeong.runninggoaltracker.shared.designsystem.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jeong.runninggoaltracker.shared.designsystem.theme.LocalAppDimensions

@Composable
fun AppContentCard(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val dimensions = LocalAppDimensions.current
    val resolvedArrangement =
        verticalArrangement ?: Arrangement.spacedBy(dimensions.cardSpacingSmall)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensions.cardElevation
        ),
        shape = RoundedCornerShape(dimensions.cardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(dimensions.cardPaddingLarge),
            verticalArrangement = resolvedArrangement,
            content = content
        )
    }
}
