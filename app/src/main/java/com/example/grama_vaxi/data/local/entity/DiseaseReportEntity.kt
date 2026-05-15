package com.example.grama_vaxi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class DiseaseReportEntity(
    @PrimaryKey val id: String,
    val ownerUid: String,
    val animalId: String,
    val symptoms: String,
    val affectedCount: Int,
    val notes: String,
    val status: String,
    val createdAt: Long,
    val synced: Boolean
)
