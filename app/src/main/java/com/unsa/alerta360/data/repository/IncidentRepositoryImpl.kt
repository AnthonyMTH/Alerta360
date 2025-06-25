package com.unsa.alerta360.data.repository

import android.util.Log
import com.unsa.alerta360.data.local.dao.IncidentDao
import com.unsa.alerta360.data.local.entity.SyncStatus
import com.unsa.alerta360.data.mapper.toDomain
import com.unsa.alerta360.data.mapper.toDto
import com.unsa.alerta360.data.mapper.toEntity
import com.unsa.alerta360.data.network.IncidentApi
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class IncidentRepositoryImpl @Inject constructor(
    private val api: IncidentApi,
    private val incidentDao: IncidentDao
) : IncidentRepository {

    override suspend fun createIncident(incident: Incident): Incident? {
        return try {
            // Crear el incidente localmente primero
            val localId = UUID.randomUUID().toString()
            val incidentWithLocalId = incident.copy(id = localId)
            
            // Guardar en base de datos local con estado PENDING_SYNC
            val entity = incidentWithLocalId.toEntity().copy(
                syncStatus = SyncStatus.PENDING_SYNC,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            incidentDao.insertIncident(entity)
            
            // Intentar sincronizar inmediatamente si hay conexión
            try {
                val response = api.createIncident(incidentWithLocalId.toDto())
                if (response.isSuccessful) {
                    val remoteIncident = response.body()?.toDomain()
                    if (remoteIncident != null) {
                        // Actualizar con ID del servidor y marcar como sincronizado
                        incidentDao.updateServerIdAndStatus(
                            localId = localId,
                            serverId = remoteIncident.id,
                            status = SyncStatus.SYNCED,
                            timestamp = System.currentTimeMillis()
                        )
                        return remoteIncident.copy(id = localId) // Mantener ID local para la UI
                    }
                }
            } catch (e: Exception) {
                Log.w("IncidentRepository", "Failed to sync immediately, will retry later: ${e.message}")
            }
            
            // Retornar el incidente local aunque no se haya sincronizado
            incidentWithLocalId
            
        } catch (e: Exception) {
            Log.e("IncidentRepository", "Error creating incident locally: ${e.message}", e)
            null
        }
    }

    override suspend fun getAllIncidents(): List<Incident> {
        return try {
            // Primero intentar obtener de la base de datos local
            val localIncidents = incidentDao.getAllIncidents().map { it.toDomain() }
            
            // Intentar sincronizar con el servidor en background
            try {
                val response = api.getAllIncidents()
                if (response.isSuccessful) {
                    val remoteIncidents = response.body()?.map { it.toDomain() } ?: emptyList()
                    // Aquí podrías implementar lógica para merge de datos locales y remotos
                    Log.d("IncidentRepository", "Successfully synced ${remoteIncidents.size} incidents from server")
                }
            } catch (e: Exception) {
                Log.w("IncidentRepository", "Failed to sync from server, using local data: ${e.message}")
            }
            
            localIncidents
            
        } catch (e: Exception) {
            Log.e("IncidentRepository", "Error loading incidents: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getIncident(id: String): Incident? {
        return try {
            // Buscar primero en la base de datos local
            val localIncident = incidentDao.getIncidentByLocalId(id)
            if (localIncident != null) {
                return localIncident.toDomain()
            }
            
            // Si no está local, intentar obtener del servidor
            val response = api.getIncident(id)
            if (response.isSuccessful) {
                val remoteIncident = response.body()?.toDomain()
                Log.d("IncidentRepository", "Incident loaded from server: $remoteIncident")
                remoteIncident
            } else {
                Log.e("IncidentRepository", "Error loading incident $id: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("IncidentRepository", "Exception loading incident $id: ${e.message}", e)
            null
        }
    }
    
    // Métodos para sincronización
    override suspend fun getPendingSyncIncidents(): List<Incident> {
        return try {
            incidentDao.getIncidentsByStatus(SyncStatus.PENDING_SYNC)
                .map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("IncidentRepository", "Error getting pending sync incidents: ${e.message}", e)
            emptyList()
        }
    }
    
    override suspend fun syncIncidentToRemote(incident: Incident): Incident {
        try {
            // Marcar como en sincronización
            incidentDao.updateSyncStatus(
                localId = incident.id,
                status = SyncStatus.SYNCING,
                timestamp = System.currentTimeMillis()
            )
            
            // Enviar al servidor
            val response = api.createIncident(incident.toDto())
            
            if (response.isSuccessful) {
                val remoteIncident = response.body()?.toDomain()
                    ?: throw Exception("Server returned null response")
                
                return remoteIncident
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
            
        } catch (e: Exception) {
            // Marcar como error de sincronización
            incidentDao.updateSyncStatus(
                localId = incident.id,
                status = SyncStatus.SYNC_ERROR,
                timestamp = System.currentTimeMillis(),
                errorMessage = e.message
            )
            
            // Incrementar contador de reintentos
            incidentDao.incrementRetryCount(incident.id, System.currentTimeMillis())
            
            throw e
        }
    }
    
    override suspend fun markIncidentAsSynced(localId: String, remoteId: String) {
        try {
            incidentDao.updateServerIdAndStatus(
                localId = localId,
                serverId = remoteId,
                status = SyncStatus.SYNCED,
                timestamp = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e("IncidentRepository", "Error marking incident as synced: ${e.message}", e)
        }
    }
    
    override suspend fun markIncidentSyncError(incidentId: String, errorMessage: String?) {
        try {
            incidentDao.updateSyncStatus(
                localId = incidentId,
                status = SyncStatus.SYNC_ERROR,
                timestamp = System.currentTimeMillis(),
                errorMessage = errorMessage
            )
        } catch (e: Exception) {
            Log.e("IncidentRepository", "Error marking sync error: ${e.message}", e)
        }
    }
}
