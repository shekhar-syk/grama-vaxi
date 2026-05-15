package com.example.grama_vaxi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.grama_vaxi.data.local.entity.AnimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {

    @Query("SELECT * FROM animals WHERE ownerUid = :ownerUid OR :ownerUid = '*' ORDER BY updatedAt DESC")
    fun observeAnimals(ownerUid: String): Flow<List<AnimalEntity>>

    @Query(
        "SELECT * FROM animals WHERE (ownerUid = :ownerUid OR :ownerUid = '*') AND nextVaccineEpochDay >= :fromEpochDay " +
            "ORDER BY nextVaccineEpochDay ASC"
    )
    fun observeUpcomingVaccines(ownerUid: String, fromEpochDay: Long): Flow<List<AnimalEntity>>

    @Query(
        "SELECT * FROM animals WHERE (ownerUid = :ownerUid OR :ownerUid = '*') AND nextVaccineEpochDay = :epochDay"
    )
    suspend fun getVaccinesForDate(ownerUid: String, epochDay: Long): List<AnimalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AnimalEntity)

    @Query("DELETE FROM animals WHERE ownerUid = :ownerUid AND id = :animalId")
    suspend fun delete(ownerUid: String, animalId: String)

    @Query("DELETE FROM animals WHERE id = :animalId")
    suspend fun deleteAny(animalId: String)

    @Query("SELECT * FROM animals WHERE id = :animalId LIMIT 1")
    suspend fun getById(animalId: String): AnimalEntity?

    @Query("SELECT COUNT(*) FROM animals WHERE ownerUid = :ownerUid")
    fun observeCount(ownerUid: String): Flow<Int>
}
