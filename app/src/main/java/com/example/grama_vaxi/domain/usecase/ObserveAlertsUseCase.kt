package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.HealthAlert
import com.example.grama_vaxi.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Streams alert items visible to the current user.
 */
class ObserveAlertsUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    operator fun invoke(ownerUid: String): Flow<List<HealthAlert>> = alertRepository.observeAlerts(ownerUid)
}
