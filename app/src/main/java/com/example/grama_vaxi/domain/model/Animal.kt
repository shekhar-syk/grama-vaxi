package com.example.grama_vaxi.domain.model

data class Animal(
    val id: String,
    val ownerUid: String,
    val tagId: String,
    val name: String,
    val type: AnimalType,
    val breed: String,
    val ageMonths: Int,
    val village: String,
    val photoUri: String? = null,
    val nextVaccineEpochDay: Long,
    val synced: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
