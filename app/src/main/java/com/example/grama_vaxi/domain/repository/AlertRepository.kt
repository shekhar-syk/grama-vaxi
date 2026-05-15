package com.example.grama_vaxi.domain.repository

import com.example.grama_vaxi.domain.model.HealthAlert
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun observeAlerts(ownerUid: String): Flow<List<HealthAlert>>
    suspend fun createCampAlert(targetVillage: String, alert: HealthAlert): Result<Unit>
    suspend fun markAlertRead(alertId: String): Result<Unit>
    suspend fun saveScannedAlert(ownerUid: String, payload: String): Result<Unit>
}
