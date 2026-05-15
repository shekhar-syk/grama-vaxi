package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.AppLanguage
import com.example.grama_vaxi.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Persists app language preference for bilingual Kannada/English UX.
 */
class SetLanguageUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(language: AppLanguage) = authRepository.setLanguage(language)
}
