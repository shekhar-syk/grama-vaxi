package com.example.grama_vaxi.data.remote.ai

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StubAiTriageService @Inject constructor() : AiTriageService {
    override suspend fun classifySymptomText(text: String): String? {
        return null
    }
}
