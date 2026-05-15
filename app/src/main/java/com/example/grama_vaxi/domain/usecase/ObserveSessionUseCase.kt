package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.SessionState
import com.example.grama_vaxi.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Streams current authentication and preference state for app startup routing.
 */
class ObserveSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<SessionState> = authRepository.sessionState()
}
