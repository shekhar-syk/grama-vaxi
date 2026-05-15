package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.AppTheme
import com.example.grama_vaxi.domain.repository.AuthRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(theme: AppTheme) = authRepository.setTheme(theme)
}
