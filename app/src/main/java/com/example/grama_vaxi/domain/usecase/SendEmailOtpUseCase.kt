package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.repository.AuthRepository
import javax.inject.Inject

class SendEmailOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<String> =
        authRepository.sendEmailOtp(email)
}
