package com.jeong.runninggoaltracker.presentation.navigation

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jeong.runninggoaltracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titleResId: Int?,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val title = stringResource(id = titleResId ?: R.string.app_name_full)

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = typography.titleMedium
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.surface,
            titleContentColor = colorScheme.onSurface
        )
    )
}
