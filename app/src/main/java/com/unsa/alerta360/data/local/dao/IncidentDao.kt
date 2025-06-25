package com.unsa.alerta360.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unsa.alerta360.data.local.entity.Incident
import kotlinx.coroutines.flow.Flow


// Rehacer en casa
@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents ORDER BY updated_at DESC")
    fun observeAll(): Flow<List<Incident>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)   // idempotente
    suspend fun insertAll(items: List<Incident>)

    @Query("SELECT etag FROM sync_state WHERE key = 'INCIDENT'")
    suspend fun lastEtag(): String?

    @Query("INSERT OR REPLACE INTO sync_state(key, etag) VALUES('INCIDENTS', :etag)")
    suspend fun saveEtag(etag: String)
}