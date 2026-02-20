package com.jeong.fitflow.feature.mypage.presentation

import com.jeong.fitflow.domain.model.AuthError
import com.jeong.fitflow.domain.model.AuthResult
import com.jeong.fitflow.domain.usecase.DeleteAccountUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DeleteAccountHandlerTest {

    @Test
    fun `handler emits loading then success when delete succeeds`() = runTest {
        val deleteAccountUseCase = mockk<DeleteAccountUseCase>()
        val handler = DeleteAccountHandler(deleteAccountUseCase)

        coEvery { deleteAccountUseCase() } returns AuthResult.Success(Unit)

        val states = handler.deleteAccount(DeleteAccountUiState.Idle)

        assertEquals(DeleteAccountUiState.Loading, states[0])
        assertEquals(DeleteAccountUiState.Success, states[1])
        coVerify(exactly = 1) { deleteAccountUseCase.invoke() }
    }

    @Test
    fun `handler emits loading then failure when delete fails`() = runTest {
        val deleteAccountUseCase = mockk<DeleteAccountUseCase>()
        val handler = DeleteAccountHandler(deleteAccountUseCase)

        coEvery { deleteAccountUseCase() } returns AuthResult.Failure(AuthError.PermissionDenied)

        val states = handler.deleteAccount(DeleteAccountUiState.Idle)

        assertEquals(DeleteAccountUiState.Loading, states[0])
        assertEquals(
            DeleteAccountUiState.Failure(AuthError.PermissionDenied),
            states[1]
        )
        coVerify(exactly = 1) { deleteAccountUseCase.invoke() }
    }
}
