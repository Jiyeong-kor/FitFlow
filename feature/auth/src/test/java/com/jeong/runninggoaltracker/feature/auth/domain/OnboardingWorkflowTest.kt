package com.jeong.runninggoaltracker.feature.auth.domain

import com.jeong.runninggoaltracker.domain.model.AuthError
import com.jeong.runninggoaltracker.domain.model.AuthProvider
import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.model.KakaoAuthExchange
import com.jeong.runninggoaltracker.domain.model.KakaoOidcToken
import com.jeong.runninggoaltracker.domain.usecase.CheckNicknameAvailabilityUseCase
import com.jeong.runninggoaltracker.domain.usecase.EnsureSignedInUseCase
import com.jeong.runninggoaltracker.domain.usecase.ExchangeKakaoOidcTokenUseCase
import com.jeong.runninggoaltracker.domain.usecase.NicknameValidationResult
import com.jeong.runninggoaltracker.domain.usecase.ReserveNicknameAndCreateUserProfileUseCase
import com.jeong.runninggoaltracker.domain.usecase.RestoreUserDataUseCase
import com.jeong.runninggoaltracker.domain.usecase.SignInAnonymouslyUseCase
import com.jeong.runninggoaltracker.domain.usecase.SignInWithCustomTokenUseCase
import com.jeong.runninggoaltracker.domain.usecase.SignInWithKakaoUseCase
import com.jeong.runninggoaltracker.domain.usecase.ValidateNicknameUseCase
import com.jeong.runninggoaltracker.shared.network.NetworkMonitor
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingWorkflowTest {

    @Test
    fun `auth failure stops nickname availability check`() = runTest {
        val validateNicknameUseCase = mockk<ValidateNicknameUseCase> {
            coEvery { invoke(any<String>()) } returns NicknameValidationResult.Valid("runner")
        }
        val networkMonitor = mockk<NetworkMonitor> {
            coEvery { isConnected() } returns true
        }
        val checkNicknameAvailabilityUseCase = mockk<CheckNicknameAvailabilityUseCase>()
        val signInAnonymouslyUseCase = mockk<SignInAnonymouslyUseCase>()
        val ensureSignedInUseCase = mockk<EnsureSignedInUseCase> {
            coEvery { invoke() } returns AuthResult.Failure(AuthError.PermissionDenied)
        }
        val reserveNicknameAndCreateUserProfileUseCase =
            mockk<ReserveNicknameAndCreateUserProfileUseCase>()
        val signInWithKakaoUseCase = mockk<SignInWithKakaoUseCase>()
        val exchangeKakaoOidcTokenUseCase = mockk<ExchangeKakaoOidcTokenUseCase>()
        val signInWithCustomTokenUseCase = mockk<SignInWithCustomTokenUseCase>()
        val restoreUserDataUseCase = mockk<RestoreUserDataUseCase>()

        val workflow = OnboardingWorkflow(
            validateNicknameUseCase = validateNicknameUseCase,
            networkMonitor = networkMonitor,
            checkNicknameAvailabilityUseCase = checkNicknameAvailabilityUseCase,
            signInAnonymouslyUseCase = signInAnonymouslyUseCase,
            ensureSignedInUseCase = ensureSignedInUseCase,
            reserveNicknameAndCreateUserProfileUseCase = reserveNicknameAndCreateUserProfileUseCase,
            signInWithKakaoUseCase = signInWithKakaoUseCase,
            exchangeKakaoOidcTokenUseCase = exchangeKakaoOidcTokenUseCase,
            signInWithCustomTokenUseCase = signInWithCustomTokenUseCase,
            restoreUserDataUseCase = restoreUserDataUseCase
        )

        workflow.continueWithNickname("runner", AuthProvider.ANONYMOUS, null)

        coVerify(exactly = 0) { checkNicknameAvailabilityUseCase.invoke(any()) }
    }

    @Test
    fun `kakao exchange sub is forwarded to auth ready state`() = runTest {
        val validateNicknameUseCase = mockk<ValidateNicknameUseCase>()
        val networkMonitor = mockk<NetworkMonitor> {
            coEvery { isConnected() } returns true
        }
        val checkNicknameAvailabilityUseCase = mockk<CheckNicknameAvailabilityUseCase>()
        val signInAnonymouslyUseCase = mockk<SignInAnonymouslyUseCase>()
        val ensureSignedInUseCase = mockk<EnsureSignedInUseCase>()
        val reserveNicknameAndCreateUserProfileUseCase =
            mockk<ReserveNicknameAndCreateUserProfileUseCase>()
        val signInWithKakaoUseCase = mockk<SignInWithKakaoUseCase> {
            coEvery { invoke() } returns Result.success(KakaoOidcToken("idToken"))
        }
        val exchangeKakaoOidcTokenUseCase = mockk<ExchangeKakaoOidcTokenUseCase> {
            coEvery { invoke(any<String>()) } returns AuthResult.Success(
                KakaoAuthExchange(customToken = "customToken", kakaoOidcSub = "sub-001")
            )
        }
        val signInWithCustomTokenUseCase = mockk<SignInWithCustomTokenUseCase> {
            coEvery { invoke(any<String>()) } returns AuthResult.Success(Unit)
        }
        val restoreUserDataUseCase = mockk<RestoreUserDataUseCase> {
            coEvery { invoke() } returns AuthResult.Success(Unit)
        }

        val workflow = OnboardingWorkflow(
            validateNicknameUseCase = validateNicknameUseCase,
            networkMonitor = networkMonitor,
            checkNicknameAvailabilityUseCase = checkNicknameAvailabilityUseCase,
            signInAnonymouslyUseCase = signInAnonymouslyUseCase,
            ensureSignedInUseCase = ensureSignedInUseCase,
            reserveNicknameAndCreateUserProfileUseCase = reserveNicknameAndCreateUserProfileUseCase,
            signInWithKakaoUseCase = signInWithKakaoUseCase,
            exchangeKakaoOidcTokenUseCase = exchangeKakaoOidcTokenUseCase,
            signInWithCustomTokenUseCase = signInWithCustomTokenUseCase,
            restoreUserDataUseCase = restoreUserDataUseCase
        )

        val result = workflow.startKakaoSession()

        val authReady = result as OnboardingResult.AuthReady
        assertEquals("sub-001", authReady.kakaoOidcSub)
    }
}
