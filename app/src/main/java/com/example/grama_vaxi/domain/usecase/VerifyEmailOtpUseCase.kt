package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.SessionState
import com.example.grama_vaxi.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyEmailOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        otpCode: String
    ): Result<Pair<SessionState, Boolean>> =
        authRepository.verifyEmailOtp(email, otpCode)
}
