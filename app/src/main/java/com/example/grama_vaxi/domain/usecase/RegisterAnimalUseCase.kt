package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.repository.AnimalRepository
import com.example.grama_vaxi.domain.repository.SyncScheduler
import javax.inject.Inject

/**
 * Registers a new animal locally first and triggers background sync for offline-first behavior.
 */
class RegisterAnimalUseCase @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val syncScheduler: SyncScheduler
) {
    suspend operator fun invoke(animal: Animal): Result<Unit> {
        val result = animalRepository.registerAnimal(animal)
        result.onSuccess { syncScheduler.enqueueImmediateSync() }
        return result
    }
}
