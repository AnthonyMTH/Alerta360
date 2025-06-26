package com.unsa.alerta360.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.unsa.alerta360.data.local.dao.IncidentDao
import com.unsa.alerta360.data.local.entity.IncidentEntity


@Database(
    entities = [IncidentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incidentDao(): IncidentDao
}