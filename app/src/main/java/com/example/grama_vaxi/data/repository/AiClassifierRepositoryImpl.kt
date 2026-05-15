package com.example.grama_vaxi.data.repository

import com.example.grama_vaxi.data.remote.ai.AiTriageService
import com.example.grama_vaxi.domain.repository.AiClassifierRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiClassifierRepositoryImpl @Inject constructor(
    private val aiTriageService: AiTriageService
) : AiClassifierRepository {

    override suspend fun classifySymptomText(text: String): String? {
        return runCatching { aiTriageService.classifySymptomText(text) }.getOrNull()
    }
}
