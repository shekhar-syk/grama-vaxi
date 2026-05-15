package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.repository.AnimalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Streams upcoming vaccinations after the provided epoch day boundary.
 */
class ObserveUpcomingVaccinesUseCase @Inject constructor(
    private val animalRepository: AnimalRepository
) {
    operator fun invoke(ownerUid: String, fromEpochDay: Long): Flow<List<Animal>> =
        animalRepository.observeUpcomingVaccines(ownerUid, fromEpochDay)
}
