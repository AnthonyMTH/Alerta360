package com.unsa.alerta360.data.local.dao

import androidx.room.*
import com.unsa.alerta360.data.local.entity.IncidentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {

    @Query("SELECT * FROM incidents")
    fun getAll(): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incidents: List<IncidentEntity>)

    @Query("DELETE FROM incidents")
    suspend fun clearAll()
}