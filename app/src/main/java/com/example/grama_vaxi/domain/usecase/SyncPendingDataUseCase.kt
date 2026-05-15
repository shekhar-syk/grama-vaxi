package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.repository.SyncRepository
import javax.inject.Inject

/**
 * Executes queued offline writes against remote services when connectivity is available.
 */
class SyncPendingDataUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(): Result<Unit> = syncRepository.syncPendingWrites()
}
