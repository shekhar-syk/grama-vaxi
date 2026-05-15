package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        userName: String,
        location: String,
        email: String,
        phoneNumber: String,
        age: String,
        roleLabel: String
    ) = authRepository.updateProfile(
        userName = userName,
        location = location,
        email = email,
        phoneNumber = phoneNumber,
        age = age,
        roleLabel = roleLabel
    )
}
