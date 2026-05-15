package com.example.grama_vaxi.di

import android.content.Context
import androidx.room.Room
import com.example.grama_vaxi.data.local.dao.AlertDao
import com.example.grama_vaxi.data.local.dao.AnimalDao
import com.example.grama_vaxi.data.local.dao.ReportDao
import com.example.grama_vaxi.data.local.dao.SyncQueueDao
import com.example.grama_vaxi.data.local.db.GramaVaxiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GramaVaxiDatabase =
        Room.databaseBuilder(context, GramaVaxiDatabase::class.java, "grama_vaxi.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideAnimalDao(database: GramaVaxiDatabase): AnimalDao = database.animalDao()

    @Provides
    fun provideAlertDao(database: GramaVaxiDatabase): AlertDao = database.alertDao()

    @Provides
    fun provideReportDao(database: GramaVaxiDatabase): ReportDao = database.reportDao()

    @Provides
    fun provideSyncQueueDao(database: GramaVaxiDatabase): SyncQueueDao = database.syncQueueDao()
}
