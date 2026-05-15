package com.example.grama_vaxi.data.remote

import android.content.Context
import com.example.grama_vaxi.data.local.dao.AnimalDao
import com.example.grama_vaxi.data.local.preferences.SessionLocalDataSource
import com.example.grama_vaxi.domain.model.DiseaseReport
import com.example.grama_vaxi.domain.model.HealthAlert
import com.example.grama_vaxi.domain.model.Animal
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.first
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val animalDao: AnimalDao
) {

    private fun firestoreOrNull(): FirebaseFirestore? {
        val configured = runCatching { FirebaseApp.getApps(context).isNotEmpty() }.getOrDefault(false)
        if (!configured) return null
        return runCatching { FirebaseFirestore.getInstance() }.getOrNull()
    }

    suspend fun upsertAnimal(animal: Animal): Result<Unit> = runCatching {
        val firestore = firestoreOrNull() ?: error("Firebase not configured")
        val session = sessionLocalDataSource.sessionFlow.first()
        val ownerUid = firebaseAuth.currentUser?.uid ?: animal.ownerUid
        val ownerName = session.userName.ifBlank { session.email.ifBlank { session.phoneNumber } }
        val nextVaccineDate = Timestamp(Date(TimeUnit.DAYS.toMillis(animal.nextVaccineEpochDay)))
        val vaccineStatus = resolveVaccineStatus(animal.nextVaccineEpochDay)
        val payload = hashMapOf(
            "ownerUid" to ownerUid,
            "ownerId" to ownerUid,
            "ownerName" to ownerName,
            "tagId" to animal.tagId,
            "name" to animal.name,
            "species" to animal.type.name,
            "type" to animal.type.name,
            "breed" to animal.breed,
            "ageMonths" to animal.ageMonths,
            "village" to animal.village,
            "photoUri" to animal.photoUri,
            "photoUrl" to animal.photoUri.orEmpty(),
            "nextVaccineEpochDay" to animal.nextVaccineEpochDay,
            "nextVaccineDate" to nextVaccineDate,
            "vaccineStatus" to vaccineStatus,
            "registeredAt" to Timestamp(Date(animal.updatedAt)),
            "updatedAt" to animal.updatedAt
        )

        firestore.collection("animals")
            .document(animal.id)
            .set(payload)
            .await()
    }

    suspend fun deleteAnimal(ownerUid: String, animalId: String): Result<Unit> = runCatching {
        val firestore = firestoreOrNull() ?: error("Firebase not configured")
        firestore.collection("animals")
            .document(animalId)
            .delete()
            .await()
    }

    suspend fun upsertReport(report: DiseaseReport): Result<Unit> = runCatching {
        val firestore = firestoreOrNull() ?: error("Firebase not configured")
        val session = sessionLocalDataSource.sessionFlow.first()
        val ownerUid = firebaseAuth.currentUser?.uid ?: report.ownerUid
        val ownerName = session.userName.ifBlank { session.email.ifBlank { session.phoneNumber } }
        val animalVillage = animalDao.getById(report.animalId)?.village.orEmpty()
        val village = animalVillage.ifBlank { session.location }
        val severity = resolveSeverity(report.symptoms, report.notes)
        val payload = hashMapOf(
            "ownerUid" to ownerUid,
            "ownerId" to ownerUid,
            "farmerId" to ownerUid,
            "ownerName" to ownerName,
            "farmerName" to ownerName,
            "animalId" to report.animalId,
            "village" to village,
            "symptoms" to report.symptoms,
            "affectedCount" to report.affectedCount,
            "notes" to report.notes,
            "status" to report.status.name,
            "severity" to severity,
            "createdAt" to Timestamp(Date(report.createdAt)),
            "reportedAt" to Timestamp(Date(report.createdAt))
        )
        firestore.collection("reports")
            .document(report.id)
            .set(payload)
            .await()
    }

    suspend fun markReportReviewed(ownerUid: String, reportId: String): Result<Unit> = runCatching {
        val firestore = firestoreOrNull() ?: error("Firebase not configured")
        firestore.collection("reports")
            .document(reportId)
            .update("status", "REVIEWED")
            .await()
    }

    suspend fun upsertAlert(alert: HealthAlert): Result<Unit> = runCatching {
        val firestore = firestoreOrNull() ?: error("Firebase not configured")
        val payload = hashMapOf(
            "ownerUid" to alert.ownerUid,
            "title" to alert.title,
            "message" to alert.message,
            "level" to alert.level.name,
            "createdAt" to alert.createdAt,
            "isRead" to alert.isRead,
            "campLocation" to alert.campLocation,
            "campTime" to alert.campTime,
            "targetVillage" to alert.targetVillage
        )
        firestore.collection("alerts")
            .document(alert.id)
            .set(payload)
            .await()
    }

    suspend fun markAlertRead(alertId: String): Result<Unit> = runCatching {
        val firestore = firestoreOrNull() ?: error("Firebase not configured")
        firestore.collection("alerts")
            .document(alertId)
            .update("isRead", true)
            .await()
    }

    private fun resolveVaccineStatus(nextVaccineEpochDay: Long): String {
        if (nextVaccineEpochDay <= 0) return "UP_TO_DATE"
        val todayEpochDay = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
        return when {
            nextVaccineEpochDay < todayEpochDay -> "OVERDUE"
            nextVaccineEpochDay - todayEpochDay <= 7 -> "UPCOMING"
            else -> "UP_TO_DATE"
        }
    }

    private fun resolveSeverity(symptoms: String, notes: String): String {
        val text = "${symptoms} ${notes}".lowercase()
        val criticalSignals = listOf(
            "high risk",
            "urgent",
            "emergency",
            "blister",
            "fever",
            "blood",
            "collapse",
            "seizure",
            "cannot stand",
            "not eating"
        )
        return if (criticalSignals.any(text::contains)) "CRITICAL" else "STANDARD"
    }
}
