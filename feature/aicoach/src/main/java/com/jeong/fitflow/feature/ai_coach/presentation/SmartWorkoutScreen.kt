package com.jeong.fitflow.feature.ai_coach.presentation

import android.content.Context
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.jeong.fitflow.domain.contract.SQUAT_FLOAT_ONE
import com.jeong.fitflow.domain.contract.SQUAT_FLOAT_TWO
import com.jeong.fitflow.domain.contract.SQUAT_FLOAT_ZERO
import com.jeong.fitflow.domain.model.ExerciseType
import com.jeong.fitflow.domain.model.PoseFrame
import com.jeong.fitflow.domain.model.PoseLandmarkType
import com.jeong.fitflow.feature.ai_coach.R
import com.jeong.fitflow.feature.ai_coach.contract.SMART_WORKOUT_ACCURACY_PERCENT_MULTIPLIER
import com.jeong.fitflow.feature.ai_coach.contract.SmartWorkoutAnimationContract
import com.jeong.fitflow.shared.designsystem.common.AppSurfaceCard
import com.jeong.fitflow.shared.designsystem.common.AppSurfaceCardPadding
import com.jeong.fitflow.shared.designsystem.common.AppSurfaceCardTone
import com.jeong.fitflow.shared.designsystem.extension.rememberThrottleClick
import com.jeong.fitflow.shared.designsystem.icon.AppIcons
import com.jeong.fitflow.shared.designsystem.theme.LocalAppDimensions
import com.jeong.fitflow.shared.designsystem.theme.LocalAppTypographyTokens
import com.jeong.fitflow.shared.designsystem.theme.RunningGoalTrackerTheme
import com.jeong.fitflow.shared.designsystem.theme.appAccentColor
import com.jeong.fitflow.shared.designsystem.theme.appSpacing2xl
import com.jeong.fitflow.shared.designsystem.theme.appSpacingLg
import com.jeong.fitflow.shared.designsystem.theme.appSpacingMd
import com.jeong.fitflow.shared.designsystem.theme.appSpacingSm
import com.jeong.fitflow.shared.designsystem.theme.appSpacingXl
import com.jeong.fitflow.shared.designsystem.theme.appTextMutedColor
import com.jeong.fitflow.shared.designsystem.theme.appTextPrimaryColor
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.math.max
import androidx.camera.core.Preview as CameraPreview
import androidx.compose.ui.tooling.preview.Preview as ComposePreview

@Composable
fun SmartWorkoutScreen(
    uiState: SmartWorkoutUiState,
    imageAnalyzer: ImageAnalysis.Analyzer,
    onBack: () -> Unit,
    onExerciseTypeChange: (ExerciseType) -> Unit
) {
    val accentColor = appAccentColor()
    val textPrimary = appTextPrimaryColor()
    val textMuted = appTextMutedColor()
    val typographyTokens = LocalAppTypographyTokens.current
    val repCountTextStyle = typographyTokens.displayHuge
    val feedbackTitleTextStyle = MaterialTheme.typography.labelSmall
    val feedbackBodyTextStyle = MaterialTheme.typography.titleMedium
    val accuracyLabelTextStyle = MaterialTheme.typography.labelSmall
    val accuracyMultiplier = SMART_WORKOUT_ACCURACY_PERCENT_MULTIPLIER
    val onBackClick = rememberThrottleClick(onClick = onBack)
    val cardTone =
        if (uiState.isPerfectForm) AppSurfaceCardTone.Emphasized else AppSurfaceCardTone.Default
    val accuracyProgress by animateFloatAsState(
        targetValue = uiState.accuracy,
        label = SmartWorkoutAnimationContract.ACCURACY_PROGRESS_ANIMATION_LABEL
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            imageAnalyzer = imageAnalyzer,
            modifier = Modifier.fillMaxSize()
        )

        if (shouldShowLandmarkOverlay(uiState.exerciseType)) {
            SkeletonOverlay(
                poseFrame = uiState.poseFrame,
                strokeColor = accentColor,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(
                    horizontal = appSpacingLg(),
                    vertical = appSpacingXl()
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = AppIcons.close(),
                    contentDescription = stringResource(R.string.smart_workout_close),
                    tint = textPrimary
                )
            }
            Box(
                modifier = Modifier.weight(SQUAT_FLOAT_ONE),
                contentAlignment = Alignment.Center
            ) {
                ExerciseTypeSelector(
                    exerciseType = uiState.exerciseType,
                    onSelect = onExerciseTypeChange
                )
            }
            Box(modifier = Modifier.width(appSpacing2xl()))
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = appSpacingXl())
        ) {
            Text(
                text = stringResource(R.string.smart_workout_rep_count_value, uiState.repCount),
                color = textPrimary,
                style = repCountTextStyle,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Black
            )
        }

        AppSurfaceCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = appSpacingLg(),
                    vertical = appSpacingXl()
                ),
            tone = cardTone,
            padding = AppSurfaceCardPadding.Large
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingSm())) {
                Text(
                    text = stringResource(R.string.smart_workout_feedback_title),
                    color = textMuted,
                    style = feedbackTitleTextStyle,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(uiState.feedbackResId),
                    color = textPrimary,
                    style = feedbackBodyTextStyle,
                    fontWeight = FontWeight.SemiBold
                )
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { accuracyProgress },
                    color = accentColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = stringResource(
                        R.string.smart_workout_accuracy_label,
                        (accuracyProgress * accuracyMultiplier).toInt()
                    ),
                    color = textMuted,
                    style = accuracyLabelTextStyle
                )
            }
        }
    }
}

@Composable
private fun ExerciseTypeSelector(
    exerciseType: ExerciseType,
    onSelect: (ExerciseType) -> Unit,
    modifier: Modifier = Modifier
) {
    val onSelectSquat = rememberThrottleClick {
        onSelect(ExerciseType.SQUAT)
    }
    val onSelectLunge = rememberThrottleClick {
        onSelect(ExerciseType.LUNGE)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(appSpacingSm())
    ) {
        ExerciseTypeOption(
            label = exerciseTypeLabel(ExerciseType.SQUAT),
            isSelected = exerciseType == ExerciseType.SQUAT,
            onClick = onSelectSquat
        )
        ExerciseTypeOption(
            label = exerciseTypeLabel(ExerciseType.LUNGE),
            isSelected = exerciseType == ExerciseType.LUNGE,
            onClick = onSelectLunge
        )
    }
}

@Composable
private fun ExerciseTypeOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        appTextPrimaryColor()
    }
    Surface(
        onClick = onClick,
        modifier = modifier.semantics(mergeDescendants = true) {
            role = Role.RadioButton
            selected = isSelected
        },
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = appSpacingMd(), vertical = appSpacingSm()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CameraPreview(
    imageAnalyzer: ImageAnalysis.Analyzer,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val previewView = remember(context) { PreviewView(context) }
    val executor = remember(context) { Executors.newSingleThreadExecutor() }
    val cameraBindingState = remember { CameraBindingState() }

    LaunchedEffect(imageAnalyzer) {
        val cameraProvider = context.awaitCameraProvider()
        cameraBindingState.cameraProvider = cameraProvider
        val preview = CameraPreview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(executor, imageAnalyzer)
            }
        cameraBindingState.analysis = analysis

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_FRONT_CAMERA,
            preview,
            analysis
        )
    }
    DisposableEffect(previewView, executor, lifecycleOwner) {
        onDispose {
            cameraBindingState.analysis?.clearAnalyzer()
            cameraBindingState.analysis = null
            cameraBindingState.cameraProvider?.unbindAll()
            cameraBindingState.cameraProvider = null
            executor.shutdown()
            (previewView.parent as? ViewGroup)?.removeView(previewView)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView }
    )
}

@Composable
private fun SkeletonOverlay(
    poseFrame: PoseFrame?,
    strokeColor: Color,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val dimensions = LocalAppDimensions.current
    val skeletonStrokeWidthPx = with(density) {
        dimensions.smartWorkoutSkeletonStrokeWidth.toPx()
    }
    val skeletonDotRadiusPx = with(density) {
        dimensions.smartWorkoutSkeletonDotRadius.toPx()
    }
    val connections = remember {
        listOf(
            PoseLandmarkType.LEFT_SHOULDER to PoseLandmarkType.RIGHT_SHOULDER,
            PoseLandmarkType.LEFT_SHOULDER to PoseLandmarkType.LEFT_HIP,
            PoseLandmarkType.RIGHT_SHOULDER to PoseLandmarkType.RIGHT_HIP,
            PoseLandmarkType.LEFT_HIP to PoseLandmarkType.RIGHT_HIP,
            PoseLandmarkType.LEFT_HIP to PoseLandmarkType.LEFT_KNEE,
            PoseLandmarkType.LEFT_KNEE to PoseLandmarkType.LEFT_ANKLE,
            PoseLandmarkType.RIGHT_HIP to PoseLandmarkType.RIGHT_KNEE,
            PoseLandmarkType.RIGHT_KNEE to PoseLandmarkType.RIGHT_ANKLE
        )
    }

    Canvas(modifier = modifier) {
        val frame = poseFrame ?: return@Canvas
        val points = frame.landmarks.associateBy { it.type }
        val imageWidth = frame.imageWidth.toFloat()
        val imageHeight = frame.imageHeight.toFloat()
        val hasValidSize = imageWidth > SQUAT_FLOAT_ZERO && imageHeight > SQUAT_FLOAT_ZERO
        val scale = if (hasValidSize) {
            max(size.width / imageWidth, size.height / imageHeight)
        } else {
            SQUAT_FLOAT_ONE
        }
        val scaledWidth = imageWidth * scale
        val scaledHeight = imageHeight * scale
        val offsetX = (size.width - scaledWidth) / SQUAT_FLOAT_TWO
        val offsetY = (size.height - scaledHeight) / SQUAT_FLOAT_TWO

        fun toOffset(landmarkX: Float, landmarkY: Float): Offset = Offset(
            x = landmarkX * scaledWidth + offsetX,
            y = landmarkY * scaledHeight + offsetY
        )

        connections.forEach { (startType, endType) ->
            val start = points[startType]
            val end = points[endType]
            if (start != null && end != null) {
                drawLine(
                    color = strokeColor,
                    start = toOffset(start.x, start.y),
                    end = toOffset(end.x, end.y),
                    strokeWidth = skeletonStrokeWidthPx,
                    cap = StrokeCap.Round
                )
            }
        }

        points.values.forEach { landmark ->
            drawCircle(
                color = strokeColor,
                radius = skeletonDotRadiusPx,
                center = toOffset(landmark.x, landmark.y)
            )
        }
    }
}

private class CameraBindingState {
    var cameraProvider: ProcessCameraProvider? = null
    var analysis: ImageAnalysis? = null
}

internal fun shouldShowLandmarkOverlay(exerciseType: ExerciseType): Boolean =
    exerciseType == ExerciseType.PUSH_UP

@Composable
private fun exerciseTypeLabel(type: ExerciseType): String = when (type) {
    ExerciseType.SQUAT -> stringResource(R.string.smart_workout_exercise_squat)
    ExerciseType.LUNGE -> stringResource(R.string.smart_workout_exercise_lunge)
    ExerciseType.PUSH_UP -> stringResource(R.string.smart_workout_exercise_push_up)
}

private suspend fun Context.awaitCameraProvider(): ProcessCameraProvider =
    suspendCancellableCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            {
                continuation.resume(future.get())
            },
            ContextCompat.getMainExecutor(this)
        )
    }

@ComposePreview(showBackground = true)
@Composable
private fun SmartWorkoutScreenPreview() {
    RunningGoalTrackerTheme {
        SmartWorkoutScreen(
            uiState = SmartWorkoutUiState(),
            imageAnalyzer = { image -> image.close() },
            onBack = {},
            onExerciseTypeChange = {},
        )
    }
}
