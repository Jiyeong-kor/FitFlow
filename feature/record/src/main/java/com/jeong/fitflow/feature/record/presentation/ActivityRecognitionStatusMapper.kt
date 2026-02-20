package com.jeong.fitflow.feature.record.presentation

import com.jeong.fitflow.domain.model.CardioActivityType
import com.jeong.fitflow.feature.record.api.model.ActivityRecognitionStatus

fun ActivityRecognitionStatus.toCardioActivityType(): CardioActivityType = when (this) {
    ActivityRecognitionStatus.Running -> CardioActivityType.Running
    ActivityRecognitionStatus.Walking -> CardioActivityType.Walking
    ActivityRecognitionStatus.OnBicycle -> CardioActivityType.OnBicycle
    else -> CardioActivityType.Other
}
