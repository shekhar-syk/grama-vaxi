package com.example.grama_vaxi.data.repository

import com.example.grama_vaxi.data.local.dao.ReportDao
import com.example.grama_vaxi.data.local.dao.SyncQueueDao
import com.example.grama_vaxi.data.local.entity.SyncQueueEntity
import com.example.grama_vaxi.data.local.mapper.toDomain
import com.example.grama_vaxi.data.local.mapper.toEntity
import com.example.grama_vaxi.domain.model.DiseaseReport
import com.example.grama_vaxi.domain.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val reportDao: ReportDao,
    private val syncQueueDao: SyncQueueDao
) : ReportRepository {

    override fun observeReports(ownerUid: String): Flow<List<DiseaseReport>> =
        reportDao.observeReports(ownerUid).map { entities -> entities.map { it.toDomain() } }

    override suspend fun submitDiseaseReport(report: DiseaseReport): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val local = report.copy(synced = false, createdAt = System.currentTimeMillis())
            reportDao.upsert(local.toEntity())
            syncQueueDao.enqueue(
                SyncQueueEntity(
                    id = UUID.randomUUID().toString(),
                    payloadType = SyncPayloadType.REPORT,
                    payloadId = local.id,
                    action = SyncAction.UPSERT,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }
}
