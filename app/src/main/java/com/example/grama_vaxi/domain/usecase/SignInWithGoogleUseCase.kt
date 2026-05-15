package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.SessionState
import com.example.grama_vaxi.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<Pair<SessionState, Boolean>> =
        authRepository.signInWithGoogle(idToken)
}
