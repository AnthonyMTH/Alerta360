package com.unsa.alerta360.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.unsa.alerta360.data.local.converter.StringListConverter

@Entity(tableName = "incidents")
@TypeConverters(StringListConverter::class)
data class IncidentEntity(
    @PrimaryKey
    val localId: String, // UUID local
    val serverId: String? = null, // ID del servidor una vez sincronizado
    val title: String,
    val description: String,
    val incidentType: String,
    val ubication: String,
    val geolocation: String,
    val evidence: List<String>,
    val district: String,
    val userId: String,
    val syncStatus: SyncStatus,
    val createdAt: Long,
    val updatedAt: Long,
    val retryCount: Int = 0,
    val lastSyncAttempt: Long? = null,
    val errorMessage: String? = null
)

enum class SyncStatus {
    PENDING_SYNC,    // Esperando a ser sincronizado
    SYNCING,         // En proceso de sincronización
    SYNCED,          // Sincronizado exitosamente
    SYNC_ERROR       // Error en la sincronización
}
