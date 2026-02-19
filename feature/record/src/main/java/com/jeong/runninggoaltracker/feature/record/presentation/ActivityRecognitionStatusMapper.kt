package com.jeong.runninggoaltracker.feature.record.presentation

import com.jeong.runninggoaltracker.domain.model.CardioActivityType
import com.jeong.runninggoaltracker.feature.record.api.model.ActivityRecognitionStatus

fun ActivityRecognitionStatus.toCardioActivityType(): CardioActivityType = when (this) {
    ActivityRecognitionStatus.Running -> CardioActivityType.Running
    ActivityRecognitionStatus.Walking -> CardioActivityType.Walking
    ActivityRecognitionStatus.OnBicycle -> CardioActivityType.OnBicycle
    else -> CardioActivityType.Other
}
