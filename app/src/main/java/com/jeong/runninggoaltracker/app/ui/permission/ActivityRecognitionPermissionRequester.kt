package com.jeong.runninggoaltracker.app.ui.permission

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberActivityRecognitionPermissionRequester():
    (onResult: (Boolean) -> Unit) -> Unit {
    var onPermissionResult by remember { mutableStateOf<(Boolean) -> Unit>({}) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onPermissionResult(granted)
    }

    return remember {
        { onResult ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                onResult(true)
            } else {
                onPermissionResult = onResult
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }
}
