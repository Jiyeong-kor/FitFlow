package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsAnonymousUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.observeIsAnonymous()
}
