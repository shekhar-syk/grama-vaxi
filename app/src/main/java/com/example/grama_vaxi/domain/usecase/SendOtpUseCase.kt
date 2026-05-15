package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Sends OTP request for login and returns a verification token.
 */
class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String): Result<String> =
        authRepository.sendOtp(phoneNumber)
}
