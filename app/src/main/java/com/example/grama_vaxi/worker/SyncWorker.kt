package com.example.grama_vaxi.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.grama_vaxi.domain.usecase.SyncPendingDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val syncPendingDataUseCase: SyncPendingDataUseCase
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val result = syncPendingDataUseCase()
        return result.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }
}
