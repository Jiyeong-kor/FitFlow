package com.jeong.runninggoaltracker.feature.auth.domain

import com.jeong.runninggoaltracker.domain.model.AuthError
import com.jeong.runninggoaltracker.domain.model.AuthResult
import com.jeong.runninggoaltracker.domain.usecase.CheckNicknameAvailabilityUseCase
import com.jeong.runninggoaltracker.domain.usecase.NicknameValidationResult
import com.jeong.runninggoaltracker.domain.usecase.ReserveNicknameAndCreateUserProfileUseCase
import com.jeong.runninggoaltracker.domain.usecase.SignInAnonymouslyUseCase
import com.jeong.runninggoaltracker.domain.usecase.SignInWithKakaoUseCase
import com.jeong.runninggoaltracker.domain.usecase.UpgradeAnonymousWithCustomTokenUseCase
import com.jeong.runninggoaltracker.domain.usecase.ValidateNicknameUseCase
import com.jeong.runninggoaltracker.shared.network.NetworkMonitor
import javax.inject.Inject

class OnboardingWorkflow @Inject constructor(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
    private val networkMonitor: NetworkMonitor,
    private val checkNicknameAvailabilityUseCase: CheckNicknameAvailabilityUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val reserveNicknameAndCreateUserProfileUseCase: ReserveNicknameAndCreateUserProfileUseCase,
    private val signInWithKakaoUseCase: SignInWithKakaoUseCase,
    private val upgradeAnonymousWithCustomTokenUseCase: UpgradeAnonymousWithCustomTokenUseCase
) {
    suspend fun continueWithNickname(nickname: String): OnboardingResult {
        val validationResult = validateNicknameUseCase(nickname)
        if (validationResult !is NicknameValidationResult.Valid) {
            return OnboardingResult.ValidationFailed(validationResult)
        }
        if (!networkMonitor.isConnected()) {
            return OnboardingResult.NoInternet
        }
        val availabilityResult = checkNicknameAvailabilityUseCase(nickname)
        if (availabilityResult is AuthResult.Success && !availabilityResult.data) {
            return OnboardingResult.Failure(OnboardingFailure.Auth(AuthError.NicknameTaken))
        }
        if (availabilityResult is AuthResult.Failure) {
            return OnboardingResult.Failure(OnboardingFailure.Auth(availabilityResult.error))
        }
        val signInResult = signInAnonymouslyUseCase()
        if (signInResult.isFailure) {
            return OnboardingResult.Failure(OnboardingFailure.AnonymousSignIn)
        }
        val nicknameResult = reserveNicknameAndCreateUserProfileUseCase(nickname)
        return when (nicknameResult) {
            is AuthResult.Success -> OnboardingResult.Success
            is AuthResult.Failure -> OnboardingResult.Failure(OnboardingFailure.Auth(nicknameResult.error))
        }
    }

    suspend fun loginWithKakao(): OnboardingResult {
        if (!networkMonitor.isConnected()) {
            return OnboardingResult.NoInternet
        }
        val signInResult = signInWithKakaoUseCase()
        val accessToken = signInResult.getOrNull()
        if (signInResult.isFailure || accessToken.isNullOrBlank()) {
            return OnboardingResult.Failure(OnboardingFailure.KakaoLogin)
        }
        val upgradeResult = upgradeAnonymousWithCustomTokenUseCase(accessToken)
        return when (upgradeResult) {
            is AuthResult.Success -> OnboardingResult.Success
            is AuthResult.Failure -> OnboardingResult.Failure(OnboardingFailure.Auth(upgradeResult.error))
        }
    }
}

sealed interface OnboardingResult {
    data object Success : OnboardingResult
    data class ValidationFailed(val result: NicknameValidationResult) : OnboardingResult
    data object NoInternet : OnboardingResult
    data class Failure(val reason: OnboardingFailure) : OnboardingResult
}

sealed interface OnboardingFailure {
    data class Auth(val error: AuthError) : OnboardingFailure
    data object AnonymousSignIn : OnboardingFailure
    data object KakaoLogin : OnboardingFailure
}
