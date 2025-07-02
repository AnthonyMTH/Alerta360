package com.unsa.alerta360.di

import com.google.firebase.messaging.FirebaseMessaging
import com.unsa.alerta360.data.network.FcmApiService
import com.unsa.alerta360.data.repository.FcmRepositoryImpl
import com.unsa.alerta360.domain.repository.FcmRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FcmModule {

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    fun provideFcmApiService(retrofit: Retrofit): FcmApiService =
        retrofit.create(FcmApiService::class.java)

    @Provides
    @Singleton
    fun provideFcmRepository(
        fcmApiService: FcmApiService,
        firebaseMessaging: FirebaseMessaging
    ): FcmRepository = FcmRepositoryImpl(fcmApiService, firebaseMessaging)
}