package com.example.grama_vaxi.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.grama_vaxi.data.local.dao.AlertDao
import com.example.grama_vaxi.data.local.dao.AnimalDao
import com.example.grama_vaxi.data.local.dao.ReportDao
import com.example.grama_vaxi.data.local.dao.SyncQueueDao
import com.example.grama_vaxi.data.local.entity.AlertEntity
import com.example.grama_vaxi.data.local.entity.AnimalEntity
import com.example.grama_vaxi.data.local.entity.DiseaseReportEntity
import com.example.grama_vaxi.data.local.entity.SyncQueueEntity

@Database(
    entities = [
        AnimalEntity::class,
        AlertEntity::class,
        DiseaseReportEntity::class,
        SyncQueueEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class GramaVaxiDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun alertDao(): AlertDao
    abstract fun reportDao(): ReportDao
    abstract fun syncQueueDao(): SyncQueueDao
}
