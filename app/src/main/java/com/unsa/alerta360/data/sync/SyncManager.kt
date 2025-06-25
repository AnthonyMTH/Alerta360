package com.unsa.alerta360.data.sync

import android.util.Log
import com.unsa.alerta360.data.local.dao.IncidentDao
import com.unsa.alerta360.data.local.entity.SyncStatus
import com.unsa.alerta360.data.mapper.toDto
import com.unsa.alerta360.data.mapper.toDomain
import com.unsa.alerta360.data.network.IncidentApi
import com.unsa.alerta360.data.network.util.NetworkUtil
import com.unsa.alerta360.data.network.util.NetworkState
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val incidentDao: IncidentDao,
    private val incidentApi: IncidentApi,
    private val networkUtil: NetworkUtil
) {
    
    companion object {
        private const val TAG = "SyncManager"
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_BASE = 1000L // 1 segundo base
    }
    
    /**
     * Sincroniza todos los incidentes pendientes
     */
    suspend fun syncPendingIncidents(): SyncResult {
        return try {
            val networkState = networkUtil.networkStateFlow().first()
            if (networkState == NetworkState.UNAVAILABLE) {
                Log.w(TAG, "No network available, skipping sync")
                return SyncResult.NoNetwork
            }
            
            val pendingIncidents = incidentDao.getIncidentsByStatuses(
                listOf(SyncStatus.PENDING_SYNC, SyncStatus.SYNC_ERROR)
            )
            
            if (pendingIncidents.isEmpty()) {
                Log.d(TAG, "No pending incidents to sync")
                return SyncResult.Success(0)
            }
            
            Log.d(TAG, "Starting sync for ${pendingIncidents.size} incidents")
            
            var successCount = 0
            var failCount = 0
            
            for (incident in pendingIncidents) {
                val result = syncSingleIncident(incident.localId)
                when (result) {
                    is SyncResult.Success -> successCount++
                    else -> failCount++
                }
            }
            
            Log.d(TAG, "Sync completed: $successCount success, $failCount failed")
            return SyncResult.Success(successCount)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during sync", e)
            return SyncResult.Error(e.message ?: "Unknown sync error")
        }
    }
    
    /**
     * Sincroniza un incidente específico
     */
    suspend fun syncSingleIncident(localId: String): SyncResult {
        return try {
            val incident = incidentDao.getIncidentByLocalId(localId)
            if (incident == null) {
                Log.w(TAG, "Incident not found: $localId")
                return SyncResult.Error("Incident not found")
            }
            
            if (incident.syncStatus == SyncStatus.SYNCED) {
                Log.d(TAG, "Incident already synced: $localId")
                return SyncResult.Success(1)
            }
            
            // Verificar si ya excedió el límite de reintentos
            if (incident.retryCount >= MAX_RETRY_COUNT) {
                Log.w(TAG, "Max retry count exceeded for incident: $localId")
                return SyncResult.Error("Max retry count exceeded")
            }
            
            // Marcar como sincronizando
            incidentDao.updateSyncStatus(
                localId = localId,
                status = SyncStatus.SYNCING,
                timestamp = System.currentTimeMillis()
            )
            
            // Intentar sincronizar con el servidor
            val response = incidentApi.createIncident(incident.toDto())
            
            if (response.isSuccessful) {
                val syncedIncident = response.body()?.toDomain()
                if (syncedIncident?._id != null) {
                    // Actualizar con el ID del servidor
                    incidentDao.updateServerIdAndStatus(
                        localId = localId,
                        serverId = syncedIncident._id,
                        status = SyncStatus.SYNCED,
                        timestamp = System.currentTimeMillis()
                    )
                    Log.d(TAG, "Successfully synced incident: $localId -> ${syncedIncident._id}")
                    return SyncResult.Success(1)
                } else {
                    throw Exception("Server response missing ID")
                }
            } else {
                val errorMsg = "HTTP ${response.code()}: ${response.message()}"
                throw Exception(errorMsg)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing incident $localId", e)
            
            // Incrementar contador de reintentos y marcar como error
            incidentDao.incrementRetryCount(localId, System.currentTimeMillis())
            incidentDao.updateSyncStatus(
                localId = localId,
                status = SyncStatus.SYNC_ERROR,
                timestamp = System.currentTimeMillis(),
                errorMessage = e.message
            )
            
            return SyncResult.Error(e.message ?: "Unknown error")
        }
    }
    
    /**
     * Obtiene estadísticas de sincronización
     */
    suspend fun getSyncStats(): SyncStats {
        return SyncStats(
            pending = incidentDao.getCountByStatus(SyncStatus.PENDING_SYNC),
            syncing = incidentDao.getCountByStatus(SyncStatus.SYNCING),
            synced = incidentDao.getCountByStatus(SyncStatus.SYNCED),
            error = incidentDao.getCountByStatus(SyncStatus.SYNC_ERROR)
        )
    }
    
    /**
     * Reinicia los incidentes que están marcados como "sincronizando"
     * Útil al iniciar la app para recuperar de estados inconsistentes
     */
    suspend fun resetSyncingIncidents() {
        val syncingIncidents = incidentDao.getIncidentsByStatus(SyncStatus.SYNCING)
        syncingIncidents.forEach { incident ->
            incidentDao.updateSyncStatus(
                localId = incident.localId,
                status = SyncStatus.PENDING_SYNC,
                timestamp = System.currentTimeMillis()
            )
        }
        Log.d(TAG, "Reset ${syncingIncidents.size} syncing incidents to pending")
    }
    
    /**
     * Reintenta incidentes con error de sincronización
     */
    suspend fun retrySyncErrors(): SyncResult {
        val errorIncidents = incidentDao.getIncidentsByStatus(SyncStatus.SYNC_ERROR)
            .filter { it.retryCount < MAX_RETRY_COUNT }
            
        if (errorIncidents.isEmpty()) {
            return SyncResult.Success(0)
        }
        
        // Resetear estado para reintento
        errorIncidents.forEach { incident ->
            incidentDao.updateSyncStatus(
                localId = incident.localId,
                status = SyncStatus.PENDING_SYNC,
                timestamp = System.currentTimeMillis()
            )
        }
        
        return syncPendingIncidents()
    }
    
    /**
     * Fuerza la sincronización de un incidente específico
     */
    suspend fun forceSyncIncident(localId: String): SyncResult {
        // Resetear estado de error si existe
        val incident = incidentDao.getIncidentByLocalId(localId)
        if (incident?.syncStatus == SyncStatus.SYNC_ERROR) {
            incidentDao.updateSyncStatus(
                localId = localId,
                status = SyncStatus.PENDING_SYNC,
                timestamp = System.currentTimeMillis()
            )
        }
        
        return syncSingleIncident(localId)
    }
}

/**
 * Resultado de operaciones de sincronización
 */
sealed class SyncResult {
    data class Success(val syncedCount: Int) : SyncResult()
    data class Error(val message: String) : SyncResult()
    object NoNetwork : SyncResult()
}

/**
 * Estadísticas de sincronización
 */
data class SyncStats(
    val pending: Int,
    val syncing: Int,
    val synced: Int,
    val error: Int
) {
    val total: Int get() = pending + syncing + synced + error
    val hasErrors: Boolean get() = error > 0
    val hasPending: Boolean get() = pending > 0 || syncing > 0
}
