package com.example.grama_vaxi.domain.model

data class HealthAlert(
    val id: String,
    val ownerUid: String,
    val title: String,
    val message: String,
    val level: AlertLevel,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetVillage: String? = null,
    val campLocation: String? = null,
    val campTime: String? = null,
    val campDateEpochDay: Long? = null,
    val synced: Boolean = false
)
