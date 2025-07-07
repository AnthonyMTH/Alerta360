package com.unsa.alerta360.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideLocationService(@ApplicationContext context: Context, fusedLocationClient: FusedLocationProviderClient): com.unsa.alerta360.data.service.LocationService {
        return com.unsa.alerta360.data.service.LocationService(context, fusedLocationClient)
    }
}
