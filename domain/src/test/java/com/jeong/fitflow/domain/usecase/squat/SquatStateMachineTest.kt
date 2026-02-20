package com.jeong.fitflow.domain.usecase.squat

import com.jeong.fitflow.domain.model.SquatState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SquatStateMachineTest {
    @Test
    fun `state machine detects a full repetition`() {
        val stateMachine = SquatStateMachine(
            hysteresisFrames = 2,
            standingAngleThreshold = 165f,
            descendingAngleThreshold = 150f,
            bottomAngleThreshold = 105f,
            ascendingAngleThreshold = 115f,
            repCompleteAngleThreshold = 165f
        )

        assertEquals(SquatState.STANDING, stateMachine.update(170f, true).state)
        assertEquals(SquatState.STANDING, stateMachine.update(145f, true).state)
        assertEquals(SquatState.DESCENDING, stateMachine.update(145f, true).state)
        assertEquals(SquatState.DESCENDING, stateMachine.update(100f, true).state)
        assertEquals(SquatState.BOTTOM, stateMachine.update(100f, true).state)
        assertEquals(SquatState.BOTTOM, stateMachine.update(120f, true).state)
        assertEquals(SquatState.ASCENDING, stateMachine.update(120f, true).state)
        assertEquals(SquatState.ASCENDING, stateMachine.update(170f, true).state)
        val repComplete = stateMachine.update(170f, true)

        assertEquals(SquatState.REP_COMPLETE, repComplete.state)
        assertTrue(repComplete.isRepCompleted)
    }
}
