package com.example.grama_vaxi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.grama_vaxi.data.local.entity.SyncQueueEntity

@Dao
interface SyncQueueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(entity: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue ORDER BY createdAt ASC")
    suspend fun getAll(): List<SyncQueueEntity>

    @Query("DELETE FROM sync_queue WHERE id = :queueId")
    suspend fun deleteById(queueId: String)

    @Query("DELETE FROM sync_queue WHERE payloadType = :payloadType AND payloadId = :payloadId AND action = :action")
    suspend fun deleteByPayload(payloadType: String, payloadId: String, action: String)
}
