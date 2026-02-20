package com.jeong.fitflow.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.jeong.fitflow", appContext.packageName)
        assertEquals(
            "com.jeong.fitflow.app.HiltTestRunner",
            HiltTestRunner::class.java.name
        )
    }
}
