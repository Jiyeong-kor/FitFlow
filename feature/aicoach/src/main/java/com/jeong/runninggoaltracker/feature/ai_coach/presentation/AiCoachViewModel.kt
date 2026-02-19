package com.jeong.runninggoaltracker.feature.ai_coach.presentation

import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.contract.SQUAT_FLOAT_ZERO
import com.jeong.runninggoaltracker.domain.contract.SQUAT_INT_ZERO
import com.jeong.runninggoaltracker.domain.model.ExerciseType
import com.jeong.runninggoaltracker.domain.model.PostureFeedbackType
import com.jeong.runninggoaltracker.domain.util.DateProvider
import com.jeong.runninggoaltracker.feature.ai_coach.contract.SmartWorkoutLogContract
import com.jeong.runninggoaltracker.feature.ai_coach.domain.WorkoutRecordSaver
import com.jeong.runninggoaltracker.feature.ai_coach.logging.WorkoutAnalyticsLogger
import com.jeong.runninggoaltracker.feature.ai_coach.processing.PoseFrameProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AiCoachViewModel @Inject constructor(
    private val poseFrameProcessor: PoseFrameProcessor,
    private val workoutRecordSaver: WorkoutRecordSaver,
    private val analyticsLogger: WorkoutAnalyticsLogger,
    private val dateProvider: DateProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmartWorkoutUiState())
    val uiState: StateFlow<SmartWorkoutUiState> = _uiState.asStateFlow()
    private val _speechEvents = MutableSharedFlow<SmartWorkoutSpeechEvent>(
        extraBufferCapacity = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val speechEvents = _speechEvents.asSharedFlow()

    val imageAnalyzer: ImageAnalysis.Analyzer
        get() = poseFrameProcessor.imageAnalyzer

    init {
        poseFrameProcessor.results(_uiState)
            .onEach { result ->
                result.speechEvent?.let { event ->
                    _speechEvents.tryEmit(event)
                }
                _uiState.value = result.uiState
            }
            .launchIn(viewModelScope)
    }

    fun updateSpeechCooldown(cooldownMs: Long) {
        poseFrameProcessor.updateSpeechCooldown(cooldownMs)
    }

    fun logUiRepCount(repCount: Int) {
        analyticsLogger.logRepCountUpdate(
            source = SmartWorkoutLogContract.SOURCE_UI,
            repCount = repCount,
            timestampMs = dateProvider.getToday()
        )
    }

    fun updateExerciseType(exerciseType: ExerciseType) {
        val currentState = _uiState.value
        if (currentState.exerciseType == exerciseType) {
            return
        }
        viewModelScope.launch {
            workoutRecordSaver.persistIfNeeded(
                exerciseType = currentState.exerciseType,
                repCount = currentState.repCount
            )
        }
        _uiState.update { current ->
            current.copy(
                exerciseType = exerciseType,
                repCount = SQUAT_INT_ZERO,
                feedbackType = PostureFeedbackType.UNKNOWN,
                feedbackKeys = emptyList(),
                accuracy = SQUAT_FLOAT_ZERO,
                isPerfectForm = false,
                overlayMode = if (current.overlayMode == DebugOverlayMode.OFF) {
                    current.overlayMode
                } else {
                    poseFrameProcessor.overlayModeFor(exerciseType)
                }
            )
        }
        poseFrameProcessor.resetSpeechState()
    }

    fun persistWorkoutRepCount() {
        val repCount = _uiState.value.repCount
        val exerciseType = _uiState.value.exerciseType
        viewModelScope.launch {
            workoutRecordSaver.persistIfNeeded(exerciseType = exerciseType, repCount = repCount)
        }
    }

    override fun onCleared() {
        persistWorkoutRepCount()
        poseFrameProcessor.clear()
        super.onCleared()
    }
}
