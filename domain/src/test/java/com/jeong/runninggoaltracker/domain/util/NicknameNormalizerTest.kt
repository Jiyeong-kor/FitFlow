package com.jeong.runninggoaltracker.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NicknameNormalizerTest {
    @Test
    fun normalizeTrimsAndLowercasesNickname() {
        val normalized = NicknameNormalizer.normalize("  RunNER  ")

        assertEquals("runner", normalized)
    }
}
