package com.example.grama_vaxi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val id: String,
    val ownerUid: String,
    val tagId: String,
    val name: String,
    val type: String,
    val breed: String,
    val ageMonths: Int,
    val village: String,
    val photoUri: String?,
    val nextVaccineEpochDay: Long,
    val synced: Boolean,
    val updatedAt: Long
)
