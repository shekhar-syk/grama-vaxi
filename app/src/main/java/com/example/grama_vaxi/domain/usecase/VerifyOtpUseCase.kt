package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.SessionState
import com.example.grama_vaxi.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Verifies OTP and establishes a local authenticated farmer session.
 */
class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        verificationToken: String,
        otpCode: String
    ): Result<Pair<SessionState, Boolean>> = authRepository.verifyOtp(verificationToken, otpCode)
}
