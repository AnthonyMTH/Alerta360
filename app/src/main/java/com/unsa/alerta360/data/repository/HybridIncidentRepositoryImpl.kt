package com.unsa.alerta360.data.repository

import android.util.Log
import com.unsa.alerta360.data.local.dao.IncidentDao
import com.unsa.alerta360.data.local.entity.SyncStatus
import com.unsa.alerta360.data.mapper.toDomain
import com.unsa.alerta360.data.mapper.toEntity
import com.unsa.alerta360.data.network.IncidentApi
import com.unsa.alerta360.data.network.util.NetworkUtil
import com.unsa.alerta360.data.network.util.NetworkState
import com.unsa.alerta360.data.sync.SyncManager
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class HybridIncidentRepositoryImpl @Inject constructor(
    private val incidentDao: IncidentDao,
    private val incidentApi: IncidentApi,
    private val networkUtil: NetworkUtil,
    private val syncManager: SyncManager
) : IncidentRepository {

    companion object {
        private const val TAG = "HybridIncidentRepository"
    }

    /**
     * Crea un incidente usando el enfoque híbrido:
     * 1. Guarda inmediatamente en la base de datos local
     * 2. Si hay conexión, intenta sincronizar inmediatamente
     * 3. Si no hay conexión, programa para sincronización posterior
     */
    override suspend fun createIncident(incident: Incident): Incident? {
        return try {
            // Generar ID local único
            val localId = UUID.randomUUID().toString()
            val incidentWithLocalId = incident.copy(_id = localId)
            
            // Determinar el estado inicial basado en la conectividad
            val networkState = networkUtil.networkStateFlow().first()
            val initialStatus = if (networkState != NetworkState.UNAVAILABLE) {
                SyncStatus.PENDING_SYNC
            } else {
                SyncStatus.PENDING_SYNC // Siempre pendiente, el sync decidirá qué hacer
            }
            
            // Guardar en la base de datos local inmediatamente
            val entityToSave = incidentWithLocalId.toEntity(initialStatus)
            incidentDao.insertIncident(entityToSave)
            
            Log.d(TAG, "Incident saved locally with ID: $localId")
            
            // Si hay conexión, intentar sincronizar inmediatamente
            if (networkState != NetworkState.UNAVAILABLE) {
                Log.d(TAG, "Network available, attempting immediate sync")
                val syncResult = syncManager.syncSingleIncident(localId)
                when (syncResult) {
                    is com.unsa.alerta360.data.sync.SyncResult.Success -> {
                        Log.d(TAG, "Incident synced successfully")
                        // Obtener el incidente actualizado con el server ID
                        val syncedEntity = incidentDao.getIncidentByLocalId(localId)
                        return syncedEntity?.toDomain()
                    }
                    else -> {
                        Log.w(TAG, "Immediate sync failed, will retry later")
                        // Aún así devolvemos el incidente local
                        return incidentWithLocalId
                    }
                }
            } else {
                Log.d(TAG, "No network, incident will sync when connection is available")
                return incidentWithLocalId
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating incident", e)
            null
        }
    }

    /**
     * Obtiene todos los incidentes usando enfoque híbrido:
     * 1. Siempre devuelve datos locales primero (offline-first)
     * 2. Si hay conexión, obtiene datos del servidor en background
     * 3. Fusiona y actualiza la base de datos local
     */
    override suspend fun getAllIncidents(): List<Incident> {
        return try {
            // Primero, obtener todos los incidentes locales
            val localIncidents = incidentDao.getAllIncidents().map { it.toDomain() }
            Log.d(TAG, "Loaded ${localIncidents.size} incidents from local database")
            
            // Si hay conexión, intentar obtener datos del servidor
            val networkState = networkUtil.networkStateFlow().first()
            if (networkState != NetworkState.UNAVAILABLE) {
                try {
                    Log.d(TAG, "Network available, fetching server data")
                    val response = incidentApi.getAllIncidents()
                    if (response.isSuccessful) {
                        val serverIncidents = response.body()?.map { dto ->
                            dto.toDomain()
                        } ?: emptyList()
                        
                        Log.d(TAG, "Fetched ${serverIncidents.size} incidents from server")
                        
                        // Actualizar la base de datos local con datos del servidor
                        // (Solo los que ya están sincronizados)
                        serverIncidents.forEach { serverIncident ->
                            serverIncident._id?.let { serverId ->
                                val existingEntity = incidentDao.getIncidentByServerId(serverId)
                                if (existingEntity != null) {
                                    // Actualizar incidente existente
                                    val updatedEntity = existingEntity.copy(
                                        title = serverIncident.title,
                                        description = serverIncident.description,
                                        incidentType = serverIncident.incidentType,
                                        ubication = serverIncident.ubication,
                                        geolocation = serverIncident.geolocation,
                                        evidence = serverIncident.evidence,
                                        district = serverIncident.district,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    incidentDao.updateIncident(updatedEntity)
                                }
                            }
                        }
                        
                        // Devolver la combinación de datos locales y servidor
                        return incidentDao.getAllIncidents().map { it.toDomain() }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to fetch server data, using local data only", e)
                }
            }
            
            // Devolver solo datos locales
            localIncidents
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading incidents", e)
            emptyList()
        }
    }
    
    /**
     * Obtiene un incidente específico por ID
     */
    override suspend fun getIncident(id: String): Incident? {
        return try {
            // Buscar primero por server ID, luego por local ID
            val entity = incidentDao.getIncidentByServerId(id) 
                ?: incidentDao.getIncidentByLocalId(id)
            
            entity?.toDomain()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading incident $id", e)
            null
        }
    }
    
    /**
     * Obtiene un Flow de todos los incidentes para UI reactiva
     */
    fun getAllIncidentsFlow(): Flow<List<Incident>> {
        return incidentDao.getAllIncidentsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    /**
     * Obtiene estadísticas de sincronización
     */
    suspend fun getSyncStats() = syncManager.getSyncStats()
    
    /**
     * Fuerza la sincronización de incidentes pendientes
     */
    suspend fun forceSyncPending() = syncManager.syncPendingIncidents()
    
    /**
     * Reintenta incidentes con errores
     */
    suspend fun retrySyncErrors() = syncManager.retrySyncErrors()
    
    // Implementaciones requeridas por la interfaz IncidentRepository
    
    /**
     * Obtiene incidentes pendientes de sincronización
     */
    override suspend fun getPendingSyncIncidents(): List<Incident> {
        return try {
            val pendingEntities = incidentDao.getIncidentsByStatuses(
                listOf(SyncStatus.PENDING_SYNC, SyncStatus.SYNC_ERROR)
            )
            pendingEntities.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting pending sync incidents", e)
            emptyList()
        }
    }
    
    /**
     * Sincroniza un incidente específico al servidor remoto
     */
    override suspend fun syncIncidentToRemote(incident: Incident): Incident {
        val localId = incident._id ?: throw IllegalArgumentException("Incident must have an ID")
        
        val entity = incidentDao.getIncidentByLocalId(localId)
            ?: throw IllegalArgumentException("Incident not found in local database")
        
        try {
            val response = incidentApi.createIncident(entity.toDto())
            if (response.isSuccessful) {
                val syncedIncident = response.body()?.toDomain()
                    ?: throw Exception("Server response is null")
                return syncedIncident
            } else {
                throw Exception("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing incident to remote", e)
            throw e
        }
    }
    
    /**
     * Marca un incidente como sincronizado
     */
    override suspend fun markIncidentAsSynced(localId: String, remoteId: String) {
        try {
            incidentDao.updateServerIdAndStatus(
                localId = localId,
                serverId = remoteId,
                status = SyncStatus.SYNCED,
                timestamp = System.currentTimeMillis()
            )
            Log.d(TAG, "Marked incident as synced: $localId -> $remoteId")
        } catch (e: Exception) {
            Log.e(TAG, "Error marking incident as synced", e)
            throw e
        }
    }
    
    /**
     * Marca un incidente con error de sincronización
     */
    override suspend fun markIncidentSyncError(incidentId: String, errorMessage: String?) {
        try {
            incidentDao.updateSyncStatus(
                localId = incidentId,
                status = SyncStatus.SYNC_ERROR,
                timestamp = System.currentTimeMillis(),
                errorMessage = errorMessage
            )
            incidentDao.incrementRetryCount(incidentId, System.currentTimeMillis())
            Log.d(TAG, "Marked incident sync error: $incidentId")
        } catch (e: Exception) {
            Log.e(TAG, "Error marking incident sync error", e)
            throw e
        }
    }
}
