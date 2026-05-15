package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.repository.AlertRepository
import com.example.grama_vaxi.domain.repository.SyncScheduler
import javax.inject.Inject

/**
 * Marks an alert as read and schedules sync.
 */
class MarkAlertReadUseCase @Inject constructor(
    private val alertRepository: AlertRepository,
    private val syncScheduler: SyncScheduler
) {
    suspend operator fun invoke(alertId: String): Result<Unit> {
        val result = alertRepository.markAlertRead(alertId)
        result.onSuccess { syncScheduler.enqueueImmediateSync() }
        return result
    }
}
