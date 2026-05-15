package com.example.grama_vaxi.domain.repository

interface AiClassifierRepository {
    suspend fun classifySymptomText(text: String): String?
}
