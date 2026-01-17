package com.jeong.runninggoaltracker.shared.designsystem.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import com.jeong.runninggoaltracker.shared.designsystem.R

@Composable
fun AppSurfaceCard(
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentPadding: PaddingValues? = null,
    content: @Composable () -> Unit
) {
    val resolvedShape = shape ?: RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius))
    val resolvedContentPadding = contentPadding
        ?: PaddingValues(dimensionResource(R.dimen.app_surface_card_content_padding))
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = resolvedShape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.size_zero)
        )
    ) {
        Box(modifier = Modifier.padding(resolvedContentPadding)) {
            content()
        }
    }
}
