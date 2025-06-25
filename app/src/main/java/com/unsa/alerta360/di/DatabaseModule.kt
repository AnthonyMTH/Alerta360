package com.unsa.alerta360.di

import android.content.Context
import androidx.room.Room
import com.unsa.alerta360.data.local.AppDatabase
import com.unsa.alerta360.data.local.dao.IncidentDao
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
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideIncidentDao(database: AppDatabase): IncidentDao {
        return database.incidentDao()
    }
}