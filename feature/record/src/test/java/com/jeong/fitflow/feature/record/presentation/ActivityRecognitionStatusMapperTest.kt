package com.jeong.fitflow.feature.record.presentation

import com.jeong.fitflow.domain.model.CardioActivityType
import com.jeong.fitflow.feature.record.api.model.ActivityRecognitionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ActivityRecognitionStatusMapperTest {

    @Test
    fun `maps running walking bicycle to cardio activity types`() {
        assertEquals(CardioActivityType.Running, ActivityRecognitionStatus.Running.toCardioActivityType())
        assertEquals(CardioActivityType.Walking, ActivityRecognitionStatus.Walking.toCardioActivityType())
        assertEquals(CardioActivityType.OnBicycle, ActivityRecognitionStatus.OnBicycle.toCardioActivityType())
    }

    @Test
    fun `maps non target statuses to other`() {
        assertEquals(CardioActivityType.Other, ActivityRecognitionStatus.Still.toCardioActivityType())
        assertEquals(CardioActivityType.Other, ActivityRecognitionStatus.InVehicle.toCardioActivityType())
        assertEquals(CardioActivityType.Other, ActivityRecognitionStatus.Unknown.toCardioActivityType())
    }
}
