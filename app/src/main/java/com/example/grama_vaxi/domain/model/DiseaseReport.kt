package com.example.grama_vaxi.domain.model

data class DiseaseReport(
    val id: String,
    val ownerUid: String,
    val animalId: String,
    val symptoms: String,
    val affectedCount: Int,
    val notes: String,
    val status: ReportStatus = ReportStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)
