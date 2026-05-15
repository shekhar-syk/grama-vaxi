package com.example.grama_vaxi.data.local.mapper

import com.example.grama_vaxi.data.local.entity.AlertEntity
import com.example.grama_vaxi.data.local.entity.AnimalEntity
import com.example.grama_vaxi.data.local.entity.DiseaseReportEntity
import com.example.grama_vaxi.domain.model.AlertLevel
import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.model.AnimalType
import com.example.grama_vaxi.domain.model.DiseaseReport
import com.example.grama_vaxi.domain.model.HealthAlert
import com.example.grama_vaxi.domain.model.ReportStatus

fun AnimalEntity.toDomain(): Animal = Animal(
    id = id,
    ownerUid = ownerUid,
    tagId = tagId,
    name = name,
    type = runCatching { AnimalType.valueOf(type) }.getOrDefault(AnimalType.COW),
    breed = breed,
    ageMonths = ageMonths,
    village = village,
    photoUri = photoUri,
    nextVaccineEpochDay = nextVaccineEpochDay,
    synced = synced,
    updatedAt = updatedAt
)

fun Animal.toEntity(): AnimalEntity = AnimalEntity(
    id = id,
    ownerUid = ownerUid,
    tagId = tagId,
    name = name,
    type = type.name,
    breed = breed,
    ageMonths = ageMonths,
    village = village,
    photoUri = photoUri,
    nextVaccineEpochDay = nextVaccineEpochDay,
    synced = synced,
    updatedAt = updatedAt
)

fun AlertEntity.toDomain(): HealthAlert = HealthAlert(
    id = id,
    ownerUid = ownerUid,
    title = title,
    message = message,
    level = runCatching { AlertLevel.valueOf(level) }.getOrDefault(AlertLevel.INFO),
    createdAt = createdAt,
    isRead = isRead,
    targetVillage = targetVillage,
    campLocation = campLocation,
    campTime = campTime,
    campDateEpochDay = campDateEpochDay,
    synced = synced
)

fun HealthAlert.toEntity(): AlertEntity = AlertEntity(
    id = id,
    ownerUid = ownerUid,
    title = title,
    message = message,
    level = level.name,
    createdAt = createdAt,
    isRead = isRead,
    targetVillage = targetVillage,
    campLocation = campLocation,
    campTime = campTime,
    campDateEpochDay = campDateEpochDay,
    synced = synced
)

fun DiseaseReportEntity.toDomain(): DiseaseReport = DiseaseReport(
    id = id,
    ownerUid = ownerUid,
    animalId = animalId,
    symptoms = symptoms,
    affectedCount = affectedCount,
    notes = notes,
    status = runCatching { ReportStatus.valueOf(status) }.getOrDefault(ReportStatus.PENDING),
    createdAt = createdAt,
    synced = synced
)

fun DiseaseReport.toEntity(): DiseaseReportEntity = DiseaseReportEntity(
    id = id,
    ownerUid = ownerUid,
    animalId = animalId,
    symptoms = symptoms,
    affectedCount = affectedCount,
    notes = notes,
    status = status.name,
    createdAt = createdAt,
    synced = synced
)
