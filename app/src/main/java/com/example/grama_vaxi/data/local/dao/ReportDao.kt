package com.example.grama_vaxi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.grama_vaxi.data.local.entity.DiseaseReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Query(
        "SELECT * FROM reports WHERE ownerUid = :ownerUid OR :ownerUid = '*' ORDER BY createdAt DESC"
    )
    fun observeReports(ownerUid: String): Flow<List<DiseaseReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DiseaseReportEntity)

    @Query("UPDATE reports SET status = 'REVIEWED', synced = 0 WHERE id = :reportId")
    suspend fun markReviewed(reportId: String)

    @Query("SELECT * FROM reports WHERE id = :reportId LIMIT 1")
    suspend fun getById(reportId: String): DiseaseReportEntity?

    @Query("SELECT COUNT(*) FROM reports WHERE status = 'PENDING'")
    fun observePendingCount(): Flow<Int>
}
