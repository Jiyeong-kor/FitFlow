package com.jeong.runninggoaltracker.feature.ai_coach.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.jeong.runninggoaltracker.feature.ai_coach.contract.SMART_WORKOUT_FEEDBACK_COOLDOWN_MS

@Composable
fun SmartWorkoutRoute(
    onBack: () -> Unit,
    viewModel: AiCoachViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cooldownMs = SMART_WORKOUT_FEEDBACK_COOLDOWN_MS
    val lifecycleOwner = LocalLifecycleOwner.current
    val onBackClick = {
        viewModel.persistWorkoutRepCount()
        onBack()
    }

    SmartWorkoutEffectHandler(
        viewModel = viewModel,
        cooldownMs = cooldownMs,
        repCount = uiState.repCount
    )

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.persistWorkoutRepCount()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.persistWorkoutRepCount()
        }
    }

    SmartWorkoutScreen(
        uiState = uiState,
        imageAnalyzer = viewModel.imageAnalyzer,
        onBack = onBackClick,
        onToggleDebugOverlay = viewModel::toggleDebugOverlay,
        onExerciseTypeChange = viewModel::updateExerciseType
    )
}

@Composable
private fun SmartWorkoutEffectHandler(
    viewModel: AiCoachViewModel,
    cooldownMs: Long,
    repCount: Int
) {
    val context = LocalContext.current.applicationContext
    val latestContext by rememberUpdatedState(context)
    val ttsController = remember { SmartWorkoutTtsController(context) }

    LaunchedEffect(cooldownMs) {
        viewModel.updateSpeechCooldown(cooldownMs)
    }

    LaunchedEffect(repCount, viewModel) {
        viewModel.logUiRepCount(repCount)
    }

    LaunchedEffect(viewModel, ttsController) {
        viewModel.speechEvents.collect { event ->
            val text = latestContext.getString(event.feedbackResId)
            ttsController.speak(text)
        }
    }

    DisposableEffect(ttsController) {
        onDispose {
            ttsController.shutdown()
        }
    }
}
