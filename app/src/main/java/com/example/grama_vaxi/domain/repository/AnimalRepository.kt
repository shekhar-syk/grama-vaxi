package com.example.grama_vaxi.domain.repository

import com.example.grama_vaxi.domain.model.Animal
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    fun observeAnimals(ownerUid: String): Flow<List<Animal>>
    fun observeUpcomingVaccines(ownerUid: String, fromEpochDay: Long): Flow<List<Animal>>
    suspend fun registerAnimal(animal: Animal): Result<Unit>
    suspend fun updateAnimal(animal: Animal): Result<Unit>
    suspend fun deleteAnimal(ownerUid: String, animalId: String): Result<Unit>
}
