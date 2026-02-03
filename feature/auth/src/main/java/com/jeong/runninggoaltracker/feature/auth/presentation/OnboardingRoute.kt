package com.jeong.runninggoaltracker.feature.auth.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jeong.runninggoaltracker.feature.auth.contract.OnboardingPermissionContract
import com.jeong.runninggoaltracker.feature.auth.contract.PermissionSettingsContract

@Composable
fun OnboardingRoute(
    onComplete: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    viewModel: OnboardingViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isPrivacyAccepted by viewModel.isPrivacyAccepted.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val locationGranted =
            results[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    results[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val allGranted = results.all { (permission, granted) ->
            if (permission == android.Manifest.permission.ACCESS_FINE_LOCATION ||
                permission == android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) {
                locationGranted
            } else {
                granted
            }
        }
        val permanentlyDenied =
            if (allGranted) {
                false
            } else {
                activity?.let { currentActivity ->
                    results.any { (permission, granted) ->
                        val shouldCheckPermanentlyDenied =
                            if (permission == android.Manifest.permission.ACCESS_FINE_LOCATION ||
                                permission == android.Manifest.permission.ACCESS_COARSE_LOCATION
                            ) {
                                !locationGranted
                            } else {
                                !granted
                            }
                        shouldCheckPermanentlyDenied &&
                                !ActivityCompat.shouldShowRequestPermissionRationale(
                                    currentActivity,
                                    permission
                                )
                    }
                } ?: false
            }
        viewModel.onPermissionsResult(allGranted, permanentlyDenied)
    }

    val permissionList = OnboardingPermissionContract.requiredPermissions()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                OnboardingEffect.OpenSettings -> {
                    val intent = Intent(
                        PermissionSettingsContract.ACTION_APPLICATION_DETAILS_SETTINGS
                    ).apply {
                        data = Uri.fromParts(
                            PermissionSettingsContract.PACKAGE_URI_SCHEME,
                            context.packageName,
                            null
                        )
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    OnboardingScreen(
        uiState = uiState,
        isPrivacyAccepted = isPrivacyAccepted,
        onRequestPermissions = { permissionLauncher.launch(permissionList) },
        onOpenSettings = viewModel::onOpenSettings,
        onNicknameChanged = viewModel::onNicknameChanged,
        onPrivacyAcceptedChange = viewModel::onPrivacyAcceptedChanged,
        onContinue = viewModel::onContinueWithNickname,
        onKakaoLogin = viewModel::onKakaoLoginClicked,
        onPrivacyPolicyClick = onPrivacyPolicyClick,
        onRetryInternet = viewModel::onRetryInternet,
        onDismissNoInternetDialog = viewModel::onDismissNoInternetDialog,
        onComplete = onComplete,
        modifier = modifier
    )
}
