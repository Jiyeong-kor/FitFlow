package com.jeong.fitflow.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class RunningConvertersTest {

    private val dayOfWeekConverter = DayOfWeekConverter()

    @Test
    fun `요일 집합을 직렬화하고 다시 역직렬화`() {
        val days = setOf(1, 5)

        val serialized = dayOfWeekConverter.toDays(days)
        val deserialized = dayOfWeekConverter.fromDays(serialized)

        assertEquals(setOf("1", "5"), serialized.split(",").toSet())
        assertEquals(days, deserialized)
    }

    @Test
    fun `잘못된 문자열은 무시하고 유효한 요일만 반환`() {
        val deserialized = dayOfWeekConverter.fromDays("abc,,1,10")

        assertEquals(setOf(1, 10), deserialized)
    }
}
