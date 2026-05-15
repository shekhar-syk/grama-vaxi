package com.example.grama_vaxi.di

import com.example.grama_vaxi.data.repository.AiClassifierRepositoryImpl
import com.example.grama_vaxi.data.repository.AlertRepositoryImpl
import com.example.grama_vaxi.data.repository.AnimalRepositoryImpl
import com.example.grama_vaxi.data.repository.AuthRepositoryImpl
import com.example.grama_vaxi.data.repository.ReportRepositoryImpl
import com.example.grama_vaxi.data.repository.SyncRepositoryImpl
import com.example.grama_vaxi.data.repository.SyncSchedulerImpl
import com.example.grama_vaxi.data.repository.CampReminderSchedulerImpl
import com.example.grama_vaxi.data.repository.VaccineReminderSchedulerImpl
import com.example.grama_vaxi.domain.repository.AiClassifierRepository
import com.example.grama_vaxi.domain.repository.AlertRepository
import com.example.grama_vaxi.domain.repository.AnimalRepository
import com.example.grama_vaxi.domain.repository.AuthRepository
import com.example.grama_vaxi.domain.repository.CampReminderScheduler
import com.example.grama_vaxi.domain.repository.ReportRepository
import com.example.grama_vaxi.domain.repository.SyncRepository
import com.example.grama_vaxi.domain.repository.SyncScheduler
import com.example.grama_vaxi.domain.repository.VaccineReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAnimalRepository(impl: AnimalRepositoryImpl): AnimalRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(impl: ReportRepositoryImpl): ReportRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository

    @Binds
    @Singleton
    abstract fun bindSyncScheduler(impl: SyncSchedulerImpl): SyncScheduler

    @Binds
    @Singleton
    abstract fun bindAiClassifierRepository(impl: AiClassifierRepositoryImpl): AiClassifierRepository

    @Binds
    @Singleton
    abstract fun bindVaccineReminderScheduler(impl: VaccineReminderSchedulerImpl): VaccineReminderScheduler

    @Binds
    @Singleton
    abstract fun bindCampReminderScheduler(impl: CampReminderSchedulerImpl): CampReminderScheduler
}
