package com.jeong.runninggoaltracker.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateNicknameUseCaseTest {

    private val useCase = ValidateNicknameUseCase()

    @Test
    fun `빈 문자열이면 EMPTY 반환`() {
        val result = useCase(" ")

        assertEquals(NicknameValidationResult.Error.EMPTY, result)
    }

    @Test
    fun `허용되지 않은 형식이면 INVALID_FORMAT 반환`() {
        val result = useCase("닉네임!!")

        assertEquals(NicknameValidationResult.Error.INVALID_FORMAT, result)
    }

    @Test
    fun `정상 형식이면 Valid 반환`() {
        val result = useCase("runner")

        assertTrue(result is NicknameValidationResult.Valid)
    }
}
