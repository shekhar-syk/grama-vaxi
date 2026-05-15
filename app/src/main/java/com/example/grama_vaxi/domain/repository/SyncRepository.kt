package com.example.grama_vaxi.domain.repository

interface SyncRepository {
    suspend fun syncPendingWrites(): Result<Unit>
}
