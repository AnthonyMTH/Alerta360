package com.unsa.alerta360.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.unsa.alerta360.data.local.converter.StringListConverter
import com.unsa.alerta360.data.local.dao.IncidentDao
import com.unsa.alerta360.data.local.entity.IncidentEntity

@Database(
    entities = [IncidentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class Alerta360Database : RoomDatabase() {
    
    abstract fun incidentDao(): IncidentDao
    
    companion object {
        const val DATABASE_NAME = "alerta360_database"
    }
}
