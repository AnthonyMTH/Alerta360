package com.unsa.alerta360.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unsa.alerta360.data.local.entity.IncidentEntity
import kotlinx.coroutines.flow.Flow



@Dao
interface IncidentDao {
    @Query("SELECT * FROM incident")
    fun observeAll(): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incidents: List<IncidentEntity>)

    @Query("DELETE FROM incident")
    suspend fun clearAll()

    @Query("SELECT * FROM incident")
    suspend fun getAllSync(): List<IncidentEntity>

    @Query("SELECT * FROM incident WHERE id = :id")
    suspend fun getById(id: String): IncidentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: IncidentEntity)

    @Query("SELECT * FROM incident WHERE synced = 0")
    suspend fun getPending(): List<IncidentEntity>
}