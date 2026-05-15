package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.repository.AnimalRepository
import com.example.grama_vaxi.domain.repository.SyncScheduler
import javax.inject.Inject

/**
 * Deletes an animal locally and schedules sync to propagate the deletion remotely.
 */
class DeleteAnimalUseCase @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val syncScheduler: SyncScheduler
) {
    suspend operator fun invoke(ownerUid: String, animalId: String): Result<Unit> {
        val result = animalRepository.deleteAnimal(ownerUid, animalId)
        result.onSuccess { syncScheduler.enqueueImmediateSync() }
        return result
    }
}
