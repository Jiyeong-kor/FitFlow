package com.jeong.fitflow.domain.usecase

import com.jeong.fitflow.domain.repository.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveIsAnonymousUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Flow<Boolean> = repository.observeIsAnonymous()
}
