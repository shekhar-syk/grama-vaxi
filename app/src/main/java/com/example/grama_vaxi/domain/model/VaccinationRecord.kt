package com.example.grama_vaxi.domain.model

data class VaccinationRecord(
    val id: String,
    val animalId: String,
    val vaccineName: String,
    val epochDay: Long,
    val administeredBy: String? = null
)
