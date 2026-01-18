package com.jeong.runninggoaltracker.feature.ai_coach.data.pose

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetector as MlKitPoseDetector
import com.google.mlkit.vision.pose.PoseLandmark as MlKitPoseLandmark
import com.jeong.runninggoaltracker.domain.model.PoseFrame
import com.jeong.runninggoaltracker.domain.model.PoseLandmark
import com.jeong.runninggoaltracker.domain.model.PoseLandmarkType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class MlKitPoseDetector(
    private val poseDetector: MlKitPoseDetector
) : PoseDetector, ImageAnalysis.Analyzer {
    private val frameFlow = MutableSharedFlow<PoseFrame>(extraBufferCapacity = 1)

    override val poseFrames: Flow<PoseFrame> = frameFlow

    override val imageAnalyzer: ImageAnalysis.Analyzer
        get() = this

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        if (mediaImage == null) {
            image.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
        poseDetector.process(inputImage)
            .addOnSuccessListener { pose ->
                frameFlow.tryEmit(pose.toPoseFrame(image.width, image.height))
            }
            .addOnCompleteListener {
                image.close()
            }
    }

    override fun clear() =
        poseDetector.close()
}

private fun Pose.toPoseFrame(imageWidth: Int, imageHeight: Int): PoseFrame {
    val width = imageWidth.takeIf { it > 0 } ?: 1
    val height = imageHeight.takeIf { it > 0 } ?: 1
    val landmarks = allPoseLandmarks.mapNotNull { landmark ->
        val type = landmark.landmarkType.toDomainType() ?: return@mapNotNull null
        PoseLandmark(
            type = type,
            x = (landmark.position.x / width).coerceIn(0f, 1f),
            y = (landmark.position.y / height).coerceIn(0f, 1f),
            z = landmark.position3D.z,
            confidence = landmark.inFrameLikelihood
        )
    }
    return PoseFrame(landmarks)
}

private fun Int.toDomainType(): PoseLandmarkType? = when (this) {
    MlKitPoseLandmark.NOSE -> PoseLandmarkType.NOSE
    MlKitPoseLandmark.LEFT_SHOULDER -> PoseLandmarkType.LEFT_SHOULDER
    MlKitPoseLandmark.RIGHT_SHOULDER -> PoseLandmarkType.RIGHT_SHOULDER
    MlKitPoseLandmark.LEFT_HIP -> PoseLandmarkType.LEFT_HIP
    MlKitPoseLandmark.RIGHT_HIP -> PoseLandmarkType.RIGHT_HIP
    MlKitPoseLandmark.LEFT_KNEE -> PoseLandmarkType.LEFT_KNEE
    MlKitPoseLandmark.RIGHT_KNEE -> PoseLandmarkType.RIGHT_KNEE
    MlKitPoseLandmark.LEFT_ANKLE -> PoseLandmarkType.LEFT_ANKLE
    MlKitPoseLandmark.RIGHT_ANKLE -> PoseLandmarkType.RIGHT_ANKLE
    else -> null
}
