package com.jeong.runninggoaltracker.shared.designsystem.common

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    @StringRes titleResId: Int?,
    modifier: Modifier = Modifier,
    @StringRes fallbackTitleResId: Int? = null,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val title = titleResId?.let { stringResource(id = it) }
        ?: fallbackTitleResId?.let { stringResource(id = it) }

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            if (title != null) {
                Text(
                    text = title,
                    style = typography.titleMedium
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.surface,
            titleContentColor = colorScheme.onSurface
        )
    )
}
