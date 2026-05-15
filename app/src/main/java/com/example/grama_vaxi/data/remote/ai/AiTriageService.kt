package com.example.grama_vaxi.data.remote.ai

interface AiTriageService {
    suspend fun classifySymptomText(text: String): String?
}
