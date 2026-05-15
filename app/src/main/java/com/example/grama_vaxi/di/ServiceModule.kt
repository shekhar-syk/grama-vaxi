package com.example.grama_vaxi.di

import com.example.grama_vaxi.data.remote.ai.AiTriageService
import com.example.grama_vaxi.data.remote.ai.StubAiTriageService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindAiTriageService(impl: StubAiTriageService): AiTriageService
}
