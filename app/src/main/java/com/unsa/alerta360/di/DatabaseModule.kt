package com.unsa.alerta360.di

import android.content.Context
import androidx.room.Room
import com.unsa.alerta360.data.local.database.Alerta360Database
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
    fun provideAlerta360Database(@ApplicationContext context: Context): Alerta360Database {
        return Room.databaseBuilder(
            context,
            Alerta360Database::class.java,
            Alerta360Database.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideIncidentDao(database: Alerta360Database): IncidentDao {
        return database.incidentDao()
    }
}
