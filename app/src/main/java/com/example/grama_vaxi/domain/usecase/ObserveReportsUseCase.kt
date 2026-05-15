package com.example.grama_vaxi.domain.usecase

import com.example.grama_vaxi.domain.model.DiseaseReport
import com.example.grama_vaxi.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Streams disease reports for a specific owner context.
 */
class ObserveReportsUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    operator fun invoke(ownerUid: String): Flow<List<DiseaseReport>> =
        reportRepository.observeReports(ownerUid)
}
