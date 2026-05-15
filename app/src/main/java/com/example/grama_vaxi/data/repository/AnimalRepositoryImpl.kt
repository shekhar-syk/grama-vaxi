package com.example.grama_vaxi.data.repository

import com.example.grama_vaxi.data.local.dao.AnimalDao
import com.example.grama_vaxi.data.local.dao.SyncQueueDao
import com.example.grama_vaxi.data.local.entity.SyncQueueEntity
import com.example.grama_vaxi.data.local.mapper.toDomain
import com.example.grama_vaxi.data.local.mapper.toEntity
import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.repository.AnimalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalRepositoryImpl @Inject constructor(
    private val animalDao: AnimalDao,
    private val syncQueueDao: SyncQueueDao
) : AnimalRepository {

    override fun observeAnimals(ownerUid: String): Flow<List<Animal>> =
        animalDao.observeAnimals(ownerUid).map { entities -> entities.map { it.toDomain() } }

    override fun observeUpcomingVaccines(ownerUid: String, fromEpochDay: Long): Flow<List<Animal>> =
        animalDao.observeUpcomingVaccines(ownerUid, fromEpochDay)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun registerAnimal(animal: Animal): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val local = animal.copy(synced = false, updatedAt = System.currentTimeMillis())
            animalDao.upsert(local.toEntity())
            syncQueueDao.enqueue(
                SyncQueueEntity(
                    id = UUID.randomUUID().toString(),
                    payloadType = SyncPayloadType.ANIMAL,
                    payloadId = animal.id,
                    action = SyncAction.UPSERT,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun updateAnimal(animal: Animal): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val local = animal.copy(synced = false, updatedAt = System.currentTimeMillis())
            animalDao.upsert(local.toEntity())
            syncQueueDao.enqueue(
                SyncQueueEntity(
                    id = UUID.randomUUID().toString(),
                    payloadType = SyncPayloadType.ANIMAL,
                    payloadId = animal.id,
                    action = SyncAction.UPSERT,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun deleteAnimal(ownerUid: String, animalId: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val existing = animalDao.getById(animalId)
            val resolvedOwner = existing?.ownerUid ?: ownerUid

            if (ownerUid == "*") {
                animalDao.deleteAny(animalId)
            } else {
                animalDao.delete(ownerUid, animalId)
            }

            syncQueueDao.enqueue(
                SyncQueueEntity(
                    id = UUID.randomUUID().toString(),
                    payloadType = SyncPayloadType.ANIMAL,
                    payloadId = "$resolvedOwner::$animalId",
                    action = SyncAction.DELETE,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }
}
