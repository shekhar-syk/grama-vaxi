package com.example.grama_vaxi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey val id: String,
    val ownerUid: String,
    val title: String,
    val message: String,
    val level: String,
    val createdAt: Long,
    val isRead: Boolean,
    val targetVillage: String?,
    val campLocation: String?,
    val campTime: String?,
    val campDateEpochDay: Long?,
    val synced: Boolean
)
