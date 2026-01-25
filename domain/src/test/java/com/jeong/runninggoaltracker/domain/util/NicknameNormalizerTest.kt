package com.jeong.runninggoaltracker.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NicknameNormalizerTest {

    @Test
    fun normalize_trims_and_lowercases() {
        assertEquals("runner", NicknameNormalizer.normalize("  RuNnEr  "))
    }
}
