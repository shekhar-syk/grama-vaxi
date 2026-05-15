package com.example.grama_vaxi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val id: String,
    val payloadType: String,
    val payloadId: String,
    val action: String,
    val createdAt: Long
)
