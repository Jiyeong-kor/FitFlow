package com.jeong.runninggoaltracker.feature.ai_coach.di

import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.jeong.runninggoaltracker.domain.usecase.ProcessPoseUseCase
import com.jeong.runninggoaltracker.domain.usecase.lunge.LungeAnalyzer
import com.jeong.runninggoaltracker.domain.usecase.squat.SquatAnalyzer
import com.jeong.runninggoaltracker.feature.ai_coach.data.pose.MlKitPoseDetector
import com.jeong.runninggoaltracker.feature.ai_coach.data.pose.PoseDetector
import com.jeong.runninggoaltracker.feature.ai_coach.logging.SmartWorkoutAnalyticsLogger
import com.jeong.runninggoaltracker.feature.ai_coach.logging.SmartWorkoutLogFormatter
import com.jeong.runninggoaltracker.feature.ai_coach.logging.SmartWorkoutLogger
import com.jeong.runninggoaltracker.feature.ai_coach.logging.WorkoutAnalyticsLogger
import com.jeong.runninggoaltracker.feature.ai_coach.processing.PoseFrameProcessor
import com.jeong.runninggoaltracker.feature.ai_coach.speech.DefaultSpeechCoordinator
import com.jeong.runninggoaltracker.feature.ai_coach.speech.SpeechCoordinator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AiCoachModule {
    @Provides
    @ViewModelScoped
    fun providePoseDetector(): PoseDetector {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        return MlKitPoseDetector(
            poseDetector = PoseDetection.getClient(options),
            isFrontCamera = true
        )
    }

    @Provides
    @ViewModelScoped
    fun provideLungeAnalyzer(): LungeAnalyzer {
        val debugLogger: (Any) -> Unit = { payload ->
            SmartWorkoutLogger.logDebug { payload.toString() }
        }
        return LungeAnalyzer(debugLogger = debugLogger)
    }

    @Provides
    @ViewModelScoped
    fun provideSquatAnalyzer(): SquatAnalyzer = SquatAnalyzer()

    @Provides
    @ViewModelScoped
    fun provideProcessPoseUseCase(
        lungeAnalyzer: LungeAnalyzer,
        squatAnalyzer: SquatAnalyzer
    ): ProcessPoseUseCase =
        ProcessPoseUseCase(
            lungeAnalyzer = lungeAnalyzer,
            squatAnalyzer = squatAnalyzer,
            pushUpAnalyzer = squatAnalyzer
        )

    @Provides
    @ViewModelScoped
    fun provideSpeechCoordinator(): SpeechCoordinator = DefaultSpeechCoordinator()

    @Provides
    @ViewModelScoped
    fun provideLogFormatter(): SmartWorkoutLogFormatter = SmartWorkoutLogFormatter()

    @Provides
    @ViewModelScoped
    fun provideAnalyticsLogger(
        formatter: SmartWorkoutLogFormatter
    ): WorkoutAnalyticsLogger = SmartWorkoutAnalyticsLogger(formatter)

    @Provides
    @ViewModelScoped
    fun providePoseFrameProcessor(
        poseDetector: PoseDetector,
        processPoseUseCase: ProcessPoseUseCase,
        speechCoordinator: SpeechCoordinator,
        analyticsLogger: WorkoutAnalyticsLogger
    ): PoseFrameProcessor = PoseFrameProcessor(
        poseDetector = poseDetector,
        processPoseUseCase = processPoseUseCase,
        speechCoordinator = speechCoordinator,
        analyticsLogger = analyticsLogger
    )
}
