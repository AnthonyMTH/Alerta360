package com.unsa.alerta360.di

import android.content.Context
import androidx.work.WorkManager
import com.unsa.alerta360.data.sync.SyncResultHandler
import com.unsa.alerta360.data.sync.SyncScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {
    
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideSyncResultHandler(@ApplicationContext context: Context): SyncResultHandler {
        return SyncResultHandler(context)
    }
    
    @Provides
    @Singleton
    fun provideSyncScheduler(@ApplicationContext context: Context): SyncScheduler {
        return SyncScheduler(context)
    }
}
