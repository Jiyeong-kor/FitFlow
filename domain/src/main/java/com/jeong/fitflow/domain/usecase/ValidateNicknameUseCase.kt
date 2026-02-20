package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.contract.NicknameValidationContract
import javax.inject.Inject

sealed interface NicknameValidationResult {
    data class Valid(val nickname: String) : NicknameValidationResult

    enum class Error : NicknameValidationResult {
        EMPTY,
        INVALID_FORMAT
    }
}

class ValidateNicknameUseCase @Inject constructor() {
    private val nicknameRegex = Regex(NicknameValidationContract.NICKNAME_REGEX_PATTERN)

    operator fun invoke(input: String): NicknameValidationResult {
        if (input.isBlank()) {
            return NicknameValidationResult.Error.EMPTY
        }

        return if (nicknameRegex.matches(input)) {
            NicknameValidationResult.Valid(input)
        } else {
            NicknameValidationResult.Error.INVALID_FORMAT
        }
    }
}
