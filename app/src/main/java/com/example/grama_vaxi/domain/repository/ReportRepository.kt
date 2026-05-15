package com.example.grama_vaxi.domain.repository

import com.example.grama_vaxi.domain.model.DiseaseReport
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun observeReports(ownerUid: String): Flow<List<DiseaseReport>>
    suspend fun submitDiseaseReport(report: DiseaseReport): Result<Unit>
}
