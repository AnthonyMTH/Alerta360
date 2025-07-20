package com.unsa.alerta360.di

import com.unsa.alerta360.data.network.socket.SocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {

    @Provides
    @Singleton
    fun provideSocketManager(): SocketManager {
        return SocketManager()
    }
}
