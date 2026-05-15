package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.DiseaseReport
import com.example.grama_vaxi.domain.repository.ReportRepository
import com.example.grama_vaxi.domain.repository.SyncScheduler
import javax.inject.Inject

/**
 * Submits a disease report to local storage and queues background sync.
 */
class SubmitDiseaseReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val syncScheduler: SyncScheduler
) {
    suspend operator fun invoke(report: DiseaseReport): Result<Unit> {
        val result = reportRepository.submitDiseaseReport(report)
        result.onSuccess { syncScheduler.enqueueImmediateSync() }
        return result
    }
}
