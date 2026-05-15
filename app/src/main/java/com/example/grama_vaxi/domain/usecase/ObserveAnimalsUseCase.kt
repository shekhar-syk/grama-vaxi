package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.repository.AnimalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Streams animals from the local store for the active farmer.
 */
class ObserveAnimalsUseCase @Inject constructor(
    private val animalRepository: AnimalRepository
) {
    operator fun invoke(ownerUid: String): Flow<List<Animal>> = animalRepository.observeAnimals(ownerUid)
}
