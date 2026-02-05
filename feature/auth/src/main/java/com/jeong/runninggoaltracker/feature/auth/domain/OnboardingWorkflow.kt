package com.jeong.runninggoaltracker.feature.auth.domain

import com.jeong.runninggoaltracker.domain.model.AuthError
import com.jeong.runninggoaltracker.domain.model.AuthProvider
import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.usecase.CheckNicknameAvailabilityUseCase
import com.jeong.runninggoaltracker.domain.usecase.EnsureSignedInUseCase
import com.jeong.runninggoaltracker.domain.usecase.ExchangeKakaoOidcTokenUseCase
import com.jeong.runninggoaltracker.domain.usecase.RestoreUserDataUseCase
import com.jeong.runninggoaltracker.domain.usecase.NicknameValidationResult
import com.jeong.runninggoaltracker.domain.usecase.ReserveNicknameAndCreateUserProfileUseCase
import com.jeong.runninggoaltracker.domain.usecase.SignInAnonymouslyUseCase
import com.jeong.runninggoaltracker.domain.usecase.SignInWithCustomTokenUseCase
import com.jeong.runninggoaltracker.domain.usecase.SignInWithKakaoUseCase
import com.jeong.runninggoaltracker.domain.usecase.ValidateNicknameUseCase
import com.jeong.runninggoaltracker.shared.network.NetworkMonitor
import javax.inject.Inject

class OnboardingWorkflow @Inject constructor(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
    private val networkMonitor: NetworkMonitor,
    private val checkNicknameAvailabilityUseCase: CheckNicknameAvailabilityUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val ensureSignedInUseCase: EnsureSignedInUseCase,
    private val reserveNicknameAndCreateUserProfileUseCase: ReserveNicknameAndCreateUserProfileUseCase,
    private val signInWithKakaoUseCase: SignInWithKakaoUseCase,
    private val exchangeKakaoOidcTokenUseCase: ExchangeKakaoOidcTokenUseCase,
    private val signInWithCustomTokenUseCase: SignInWithCustomTokenUseCase,
    private val restoreUserDataUseCase: RestoreUserDataUseCase
) {
    suspend fun startAnonymousSession(): OnboardingResult {
        if (!networkMonitor.isConnected()) {
            return OnboardingResult.NoInternet
        }
        val signInResult = signInAnonymouslyUseCase()
        return if (signInResult.isFailure) {
            OnboardingResult.Failure(OnboardingFailure.AnonymousSignIn)
        } else {
            OnboardingResult.AuthReady(AuthProvider.ANONYMOUS, null)
        }
    }

    suspend fun startKakaoSession(): OnboardingResult {
        if (!networkMonitor.isConnected()) {
            return OnboardingResult.NoInternet
        }
        val kakaoResult = signInWithKakaoUseCase()
        val kakaoToken = kakaoResult.getOrNull()
        if (kakaoResult.isFailure || kakaoToken?.idToken.isNullOrBlank()) {
            return OnboardingResult.Failure(OnboardingFailure.KakaoLogin)
        }
        val exchangeResult = exchangeKakaoOidcTokenUseCase(kakaoToken.idToken)
        val exchangePayload = if (exchangeResult is AuthResult.Success) {
            exchangeResult.data
        } else {
            return OnboardingResult.Failure(OnboardingFailure.KakaoLogin)
        }
        val signInResult = signInWithCustomTokenUseCase(exchangePayload.customToken)
        if (signInResult is AuthResult.Failure) {
            return OnboardingResult.Failure(OnboardingFailure.Auth(signInResult.error))
        }
        val restoreResult = restoreUserDataUseCase()
        if (restoreResult is AuthResult.Failure) {
            return OnboardingResult.Failure(OnboardingFailure.Auth(restoreResult.error))
        }
        return OnboardingResult.AuthReady(AuthProvider.KAKAO, exchangePayload.kakaoOidcSub)
    }

    suspend fun continueWithNickname(
        nickname: String,
        authProvider: AuthProvider?,
        kakaoOidcSub: String?
    ): OnboardingResult {
        if (authProvider == null) {
            return OnboardingResult.Failure(OnboardingFailure.Auth(AuthError.PermissionDenied))
        }
        val validationResult = validateNicknameUseCase(nickname)
        if (validationResult !is NicknameValidationResult.Valid) {
            return OnboardingResult.ValidationFailed(validationResult)
        }
        if (!networkMonitor.isConnected()) {
            return OnboardingResult.NoInternet
        }
        val ensureResult = ensureSignedInUseCase()
        if (ensureResult is AuthResult.Failure) {
            return OnboardingResult.Failure(OnboardingFailure.Auth(ensureResult.error))
        }
        val availabilityResult = checkNicknameAvailabilityUseCase(nickname)
        if (availabilityResult is AuthResult.Success && !availabilityResult.data) {
            return OnboardingResult.Failure(OnboardingFailure.Auth(AuthError.NicknameTaken))
        }
        if (availabilityResult is AuthResult.Failure) {
            return OnboardingResult.Failure(OnboardingFailure.Auth(availabilityResult.error))
        }
        val nicknameResult = reserveNicknameAndCreateUserProfileUseCase(
            nickname = nickname,
            authProvider = authProvider,
            kakaoOidcSub = kakaoOidcSub
        )
        return when (nicknameResult) {
            is AuthResult.Success -> OnboardingResult.Success
            is AuthResult.Failure -> OnboardingResult.Failure(OnboardingFailure.Auth(nicknameResult.error))
        }
    }
}

sealed interface OnboardingResult {
    data object Success : OnboardingResult
    data class AuthReady(val authProvider: AuthProvider, val kakaoOidcSub: String?) :
        OnboardingResult

    data class ValidationFailed(val result: NicknameValidationResult) : OnboardingResult
    data object NoInternet : OnboardingResult
    data class Failure(val reason: OnboardingFailure) : OnboardingResult
}

sealed interface OnboardingFailure {
    data class Auth(val error: AuthError) : OnboardingFailure
    data object AnonymousSignIn : OnboardingFailure
    data object KakaoLogin : OnboardingFailure
}
