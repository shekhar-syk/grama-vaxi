package com.example.grama_vaxi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.grama_vaxi.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query(
        "SELECT * FROM alerts WHERE ownerUid = :ownerUid OR ownerUid = 'all' " +
            "ORDER BY isRead ASC, createdAt DESC"
    )
    fun observeAlerts(ownerUid: String): Flow<List<AlertEntity>>

    @Query(
        "SELECT * FROM alerts WHERE (ownerUid = :ownerUid OR ownerUid = 'all') AND campDateEpochDay = :epochDay"
    )
    suspend fun getAlertsForDate(ownerUid: String, epochDay: Long): List<AlertEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AlertEntity)

    @Query("UPDATE alerts SET isRead = 1, synced = 0 WHERE id = :alertId")
    suspend fun markRead(alertId: String)

    @Query("SELECT * FROM alerts WHERE id = :alertId LIMIT 1")
    suspend fun getById(alertId: String): AlertEntity?

    @Query("SELECT COUNT(*) FROM alerts WHERE (ownerUid = :ownerUid OR ownerUid = 'all') AND isRead = 0")
    fun observeUnreadCount(ownerUid: String): Flow<Int>
}
