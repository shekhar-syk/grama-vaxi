package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.repository.AiClassifierRepository
import com.example.grama_vaxi.utils.NetworkMonitor
import javax.inject.Inject

/**
 * Classifies symptom text with online AI service when available, otherwise applies local fallback rules.
 */
class ClassifySymptomsUseCase @Inject constructor(
    private val aiClassifierRepository: AiClassifierRepository,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(symptoms: String): String {
        if (networkMonitor.isOnline()) {
            val remote = aiClassifierRepository.classifySymptomText(symptoms)
            if (!remote.isNullOrBlank()) {
                return remote
            }
        }

        val lower = symptoms.lowercase()
        return when {
            "blister" in lower || "fever" in lower -> "High risk: possible FMD. Isolate and contact vet immediately."
            "cough" in lower || "breathing" in lower -> "Respiratory concern: monitor and consult local veterinary service."
            "not eating" in lower || "letharg" in lower -> "General weakness detected: hydrate animal and request checkup."
            else -> "Symptoms recorded. Follow-up assessment recommended."
        }
    }
}
