package com.jeong.runninggoaltracker.feature.ai_coach.presentation

import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.runninggoaltracker.domain.usecase.ProcessPoseUseCase
import com.jeong.runninggoaltracker.feature.ai_coach.data.pose.PoseDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AiCoachViewModel @Inject constructor(
    private val poseDetector: PoseDetector,
    private val processPoseUseCase: ProcessPoseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmartWorkoutUiState())
    val uiState: StateFlow<SmartWorkoutUiState> = _uiState.asStateFlow()

    val imageAnalyzer: ImageAnalysis.Analyzer
        get() = poseDetector.imageAnalyzer

    init {
        poseDetector.poseFrames
            .onEach { frame ->
                val analysis = processPoseUseCase.analyze(frame, _uiState.value.exerciseType)
                _uiState.update { current ->
                    current.copy(
                        repCount = analysis.repCount.value,
                        feedbackType = analysis.feedback.type,
                        accuracy = analysis.feedback.accuracy,
                        isPerfectForm = analysis.feedback.isPerfectForm,
                        poseFrame = frame
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        poseDetector.clear()
        super.onCleared()
    }
}
