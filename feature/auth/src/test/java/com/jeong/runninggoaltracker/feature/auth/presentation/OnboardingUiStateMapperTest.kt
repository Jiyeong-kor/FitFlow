package com.jeong.runninggoaltracker.feature.auth.presentation

import com.jeong.runninggoaltracker.domain.usecase.NicknameValidationResult
import com.jeong.runninggoaltracker.feature.auth.R
import com.jeong.runninggoaltracker.feature.auth.domain.OnboardingResult
import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingUiStateMapperTest {

    private val mapper = OnboardingUiStateMapper()

    @Test
    fun `permissions granted moves to auth choice`() {
        val initial = OnboardingUiState()

        val result = mapper.applyPermissionsResult(
            currentState = initial,
            isGranted = true,
            isPermanentlyDenied = false
        )

        assertEquals(OnboardingStep.AuthChoice, result.step)
        assertEquals(null, result.permissionErrorResId)
        assertEquals(false, result.isPermissionPermanentlyDenied)
    }

    @Test
    fun `validation failure shows required error`() {
        val initial = OnboardingUiState(isLoading = true)
        val validation = OnboardingResult.ValidationFailed(NicknameValidationResult.Error.EMPTY)

        val result = mapper.applyContinueResult(initial, validation)

        assertEquals(false, result.isLoading)
        assertEquals(false, result.isNicknameValid)
        assertEquals(R.string.nickname_required_error, result.nicknameValidationMessage)
        assertEquals(false, result.shouldShowNicknameHintError)
    }
}
