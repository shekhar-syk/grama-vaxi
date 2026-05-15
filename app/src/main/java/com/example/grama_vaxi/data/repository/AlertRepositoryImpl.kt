package com.example.grama_vaxi.data.repository

import com.example.grama_vaxi.data.local.dao.AlertDao
import com.example.grama_vaxi.data.local.dao.SyncQueueDao
import com.example.grama_vaxi.data.local.entity.AlertEntity
import com.example.grama_vaxi.data.local.entity.SyncQueueEntity
import com.example.grama_vaxi.data.local.mapper.toDomain
import com.example.grama_vaxi.data.local.mapper.toEntity
import com.example.grama_vaxi.domain.model.AlertLevel
import com.example.grama_vaxi.domain.model.HealthAlert
import com.example.grama_vaxi.domain.repository.AlertRepository
import com.example.grama_vaxi.domain.repository.CampReminderScheduler
import com.example.grama_vaxi.utils.DateUtils
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepositoryImpl @Inject constructor(
    private val alertDao: AlertDao,
    private val syncQueueDao: SyncQueueDao,
    private val firestore: FirebaseFirestore,
    private val campReminderScheduler: CampReminderScheduler
) : AlertRepository {

    override fun observeAlerts(ownerUid: String): Flow<List<HealthAlert>> =
        alertDao.observeAlerts(ownerUid).map { entities -> entities.map { it.toDomain() } }

    override suspend fun createCampAlert(targetVillage: String, alert: HealthAlert): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val local = alert.copy(
                targetVillage = targetVillage,
                ownerUid = "all",
                synced = false,
                createdAt = System.currentTimeMillis()
            )
            alertDao.upsert(local.toEntity())
            syncQueueDao.enqueue(
                SyncQueueEntity(
                    id = UUID.randomUUID().toString(),
                    payloadType = SyncPayloadType.ALERT,
                    payloadId = local.id,
                    action = SyncAction.UPSERT,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun markAlertRead(alertId: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            alertDao.markRead(alertId)
            syncQueueDao.enqueue(
                SyncQueueEntity(
                    id = UUID.randomUUID().toString(),
                    payloadType = SyncPayloadType.ALERT,
                    payloadId = alertId,
                    action = SyncAction.UPSERT,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun saveScannedAlert(ownerUid: String, payload: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val json = JSONObject(payload)
            if (json.optString("type") != "camp-schedule") {
                throw IllegalArgumentException("Invalid QR code type")
            }

            val alertId = json.optString("campId").takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
            val village = json.optString("village")
            val date = json.optString("date")
            val time = json.optString("time")
            val location = json.optString("location")
            val services = json.optString("services")
            val message = json.optString("message")

            val alert = HealthAlert(
                id = alertId,
                ownerUid = ownerUid,
                title = "Health Camp: $village",
                message = message.takeIf { !it.isNullOrBlank() } ?: "Vaccination services for $services at $location",
                level = AlertLevel.INFO,
                createdAt = System.currentTimeMillis(),
                isRead = false,
                targetVillage = village,
                campLocation = location,
                campTime = time,
                campDateEpochDay = DateUtils.parseDateToEpochDay(date),
                synced = false
            )

            alertDao.upsert(alert.toEntity())
            campReminderScheduler.scheduleCampReminder(alert)
        }
    }

    suspend fun syncAlertsFromFirestore(ownerUid: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val thirtyDaysAgo = Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000))

            val userAlerts = firestore.collection("alerts")
                .whereEqualTo("ownerUid", ownerUid)
                .whereGreaterThanOrEqualTo("createdAt", thirtyDaysAgo)
                .get().await()
                
            val globalAlerts = firestore.collection("alerts")
                .whereEqualTo("ownerUid", "all")
                .whereGreaterThanOrEqualTo("createdAt", thirtyDaysAgo)
                .get().await()

            val allDocs: List<DocumentSnapshot> = userAlerts.documents + globalAlerts.documents

            for (doc in allDocs) {
                val data = doc.data ?: continue
                val createdAtMs = when (val raw = data["createdAt"]) {
                    is Timestamp -> raw.seconds * 1000L + raw.nanoseconds / 1_000_000L
                    is Number    -> raw.toLong()
                    else         -> System.currentTimeMillis()
                }
                
                val village = (data["targetVillage"] as? String) ?: (data["village"] as? String)
                
                val alertEntity = AlertEntity(
                    id = doc.id,
                    ownerUid = data["ownerUid"] as? String ?: ownerUid,
                    title = data["title"] as? String ?: "Alert",
                    message = data["message"] as? String ?: "",
                    level = data["level"] as? String ?: "INFO",
                    createdAt = createdAtMs,
                    isRead = data["isRead"] as? Boolean ?: false,
                    targetVillage = village,
                    campLocation = data["campLocation"] as? String,
                    campTime = data["campTime"] as? String,
                    campDateEpochDay = DateUtils.parseDateToEpochDay(data["campDate"] as? String),
                    synced = true
                )
                alertDao.upsert(alertEntity)
            }
        }
    }
}
