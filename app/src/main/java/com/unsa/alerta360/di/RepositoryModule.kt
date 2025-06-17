package com.unsa.alerta360.di

import com.unsa.alerta360.data.network.IncidentApi
import com.unsa.alerta360.data.repository.IncidentRepositoryImpl
import com.unsa.alerta360.domain.repository.IncidentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
/*
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideIncidentRepository(api: IncidentApi): IncidentRepository {
        return IncidentRepositoryImpl(api)
    }
}*/