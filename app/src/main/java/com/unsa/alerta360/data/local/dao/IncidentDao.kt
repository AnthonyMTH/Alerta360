package com.unsa.alerta360.data.local.dao

import androidx.room.*
import com.unsa.alerta360.data.local.entity.IncidentEntity
import com.unsa.alerta360.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    
    @Query("SELECT * FROM incidents WHERE localId = :localId")
    suspend fun getIncidentByLocalId(localId: String): IncidentEntity?
    
    @Query("SELECT * FROM incidents WHERE serverId = :serverId")
    suspend fun getIncidentByServerId(serverId: String): IncidentEntity?
    
    @Query("SELECT * FROM incidents ORDER BY createdAt DESC")
    fun getAllIncidentsFlow(): Flow<List<IncidentEntity>>
    
    @Query("SELECT * FROM incidents ORDER BY createdAt DESC")
    suspend fun getAllIncidents(): List<IncidentEntity>
    
    @Query("SELECT * FROM incidents WHERE syncStatus = :status ORDER BY createdAt ASC")
    suspend fun getIncidentsByStatus(status: SyncStatus): List<IncidentEntity>
    
    @Query("SELECT * FROM incidents WHERE syncStatus IN (:statuses) ORDER BY createdAt ASC")
    suspend fun getIncidentsByStatuses(statuses: List<SyncStatus>): List<IncidentEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity)
    
    @Update
    suspend fun updateIncident(incident: IncidentEntity)
    
    @Query("UPDATE incidents SET syncStatus = :status, lastSyncAttempt = :timestamp, errorMessage = :errorMessage WHERE localId = :localId")
    suspend fun updateSyncStatus(localId: String, status: SyncStatus, timestamp: Long, errorMessage: String? = null)
    
    @Query("UPDATE incidents SET serverId = :serverId, syncStatus = :status, lastSyncAttempt = :timestamp WHERE localId = :localId")
    suspend fun updateServerIdAndStatus(localId: String, serverId: String, status: SyncStatus, timestamp: Long)
    
    @Query("UPDATE incidents SET retryCount = retryCount + 1, lastSyncAttempt = :timestamp WHERE localId = :localId")
    suspend fun incrementRetryCount(localId: String, timestamp: Long)
    
    @Delete
    suspend fun deleteIncident(incident: IncidentEntity)
    
    @Query("DELETE FROM incidents WHERE localId = :localId")
    suspend fun deleteIncidentByLocalId(localId: String)
    
    @Query("SELECT COUNT(*) FROM incidents WHERE syncStatus = :status")
    suspend fun getCountByStatus(status: SyncStatus): Int
}
