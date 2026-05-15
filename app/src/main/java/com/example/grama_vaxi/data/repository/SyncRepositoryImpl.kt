package com.example.grama_vaxi.data.repository

import com.example.grama_vaxi.data.local.dao.AlertDao
import com.example.grama_vaxi.data.local.dao.AnimalDao
import com.example.grama_vaxi.data.local.dao.ReportDao
import com.example.grama_vaxi.data.local.dao.SyncQueueDao
import com.example.grama_vaxi.data.local.mapper.toDomain
import com.example.grama_vaxi.data.remote.FirebaseRemoteDataSource
import com.example.grama_vaxi.domain.repository.SyncRepository
import com.example.grama_vaxi.utils.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val animalDao: AnimalDao,
    private val alertDao: AlertDao,
    private val reportDao: ReportDao,
    private val syncQueueDao: SyncQueueDao,
    private val remoteDataSource: FirebaseRemoteDataSource,
    private val networkMonitor: NetworkMonitor
) : SyncRepository {

    override suspend fun syncPendingWrites(): Result<Unit> = runCatching {
        if (!networkMonitor.isOnline()) {
            error("Device offline")
        }

        withContext(Dispatchers.IO) {
            val queue = syncQueueDao.getAll()
            for (entry in queue) {
                when (entry.payloadType) {
                    SyncPayloadType.ANIMAL -> syncAnimal(entry.payloadId, entry.action)
                    SyncPayloadType.ALERT -> syncAlert(entry.payloadId)
                    SyncPayloadType.REPORT -> syncReport(entry.payloadId)
                }
                syncQueueDao.deleteById(entry.id)
            }
        }
    }

    private suspend fun syncAnimal(payloadId: String, action: String) {
        when (action) {
            SyncAction.DELETE -> {
                val parts = payloadId.split("::")
                if (parts.size == 2) {
                    remoteDataSource.deleteAnimal(parts[0], parts[1]).getOrThrow()
                }
            }

            else -> {
                val entity = animalDao.getById(payloadId) ?: return
                remoteDataSource.upsertAnimal(entity.toDomain()).getOrThrow()
                animalDao.upsert(entity.copy(synced = true))
            }
        }
    }

    private suspend fun syncAlert(payloadId: String) {
        val entity = alertDao.getById(payloadId) ?: return
        remoteDataSource.upsertAlert(entity.toDomain()).getOrThrow()
        alertDao.upsert(entity.copy(synced = true))
    }

    private suspend fun syncReport(payloadId: String) {
        val entity = reportDao.getById(payloadId) ?: return
        remoteDataSource.upsertReport(entity.toDomain()).getOrThrow()
        reportDao.upsert(entity.copy(synced = true))
    }
}
