package com.unsa.alerta360.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.unsa.alerta360.data.local.dao.IncidentDao
import com.unsa.alerta360.data.mapper.toDomain
import com.unsa.alerta360.data.mapper.toDto
import com.unsa.alerta360.data.mapper.toEntity
import com.unsa.alerta360.data.network.IncidentApi
import com.unsa.alerta360.data.network.NetworkMonitor
import com.unsa.alerta360.di.IoDispatcher
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import java.util.UUID
import javax.inject.Inject

class IncidentRepositoryImpl @Inject constructor(
    private val api: IncidentApi,
    private val dao: IncidentDao,
    private val prefs: DataStore<Preferences>,      // para el ETag
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : IncidentRepository {


    private val ETAG_KEY = stringPreferencesKey("ETAG_INCIDENTS")

    override suspend fun createIncident(domain: Incident): Incident {
        // 1) Generar ID temporal para el local
        val tempId = UUID.randomUUID().toString()
        val localEntity = domain.toEntity().copy(
            id = tempId,
            synced = false,
            createdAt = System.now().toString(),
            updatedAt = System.now().toString()
        )
        dao.insert(localEntity)

        // 2) Si hay red, intentar subir al remoto
        if (NetworkMonitor.hasNetwork(context)) {
            try {
                // Crear DTO sin ID para que MongoDB genere su propio ObjectId
                val dtoForServer = localEntity.toDto().copy(_id = null)
                val resp = api.createIncident(dtoForServer)
                if (resp.isSuccessful) {
                    val remoteDto = resp.body()!!
                    // Eliminar el registro temporal local
                    dao.deleteById(tempId)
                    // Insertar el registro con ID del servidor y marcado como sincronizado
                    val remoteEntity = remoteDto.toEntity().copy(synced = true)
                    dao.insert(remoteEntity)
                    return remoteDto.toDomain()
                }
            } catch (e: Exception) {
                Log.e("IncidentRepo", "Error uploading to server: ${e.message}", e)
            }
        }

        // 3) Devuelvo lo local si falla la subida
        return localEntity.toDomain()
    }

    override fun observeIncidents(): Flow<List<Incident>> =
        dao.observeAll()
            .onStart {
                try {
                    fetchAndCacheIncidents()
                } catch (e: Exception) {
                    Log.e("IncidentRepo", "No pude sincronizar con remoto, usar local", e)
                }
            }
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getAllIncidents(): List<Incident> = withContext(ioDispatcher) {
        fetchAndCacheIncidents()
        dao.getAllSync().map { it.toDomain() }
    }


    override suspend fun getIncident(id: String): Incident? = withContext(ioDispatcher) {
        try {
            val resp = api.getIncident(id)
            if (resp.isSuccessful) {
                resp.body()?.let { dto ->
                    // Actualizamos esa única entidad en caché
                    dao.insertAll(listOf(dto.toEntity()))
                    return@withContext dto.toDomain()
                }
            }
        } catch (e: Exception) {
            Log.e("IncidentRepo", "Exception getIncident: ${e.message}", e)
        }
        // Si falla el remoto o es 404, leemos la versión local
        dao.getById(id)?.toDomain()
    }

    override suspend fun deleteIncident(id: String): Incident? {
        return withContext(ioDispatcher) {
            try {
                // 1. Obtener el incidente local antes de eliminarlo para devolverlo
                val localIncident = dao.getById(id)?.toDomain()
                
                // 2. Eliminar de la base de datos local inmediatamente
                dao.deleteById(id)
                
                // 3. Si hay conexión, intentar eliminar del servidor
                if (NetworkMonitor.hasNetwork(context)) {
                    try {
                        // Solo intentar eliminar del servidor si el ID parece ser un ObjectId válido
                        if (isValidObjectId(id)) {
                            val resp = api.deleteIncident(id)
                            if (resp.isSuccessful) {
                                Log.d("IncidentRepo", "Incidente eliminado exitosamente del servidor")
                                return@withContext resp.body()?.toDomain() ?: localIncident
                            } else {
                                Log.w("IncidentRepo", "Error del servidor al eliminar: ${resp.code()}")
                            }
                        } else {
                            Log.d("IncidentRepo", "ID local detectado, no se puede eliminar del servidor")
                        }
                    } catch (e: Exception) {
                        Log.e("IncidentRepo", "Excepción al eliminar del servidor: ${e.message}", e)
                    }
                }
                Log.d("IncidentRepo", "Sin conexión, solo eliminado localmente")
                return@withContext localIncident
            } catch (e: Exception) {
                Log.e("IncidentRepo", "Exception deleteIncident: ${e.message}", e)
                return@withContext null
            }

        }
    }

    private fun isValidObjectId(id: String): Boolean {
        // ObjectId de MongoDB tiene 24 caracteres hexadecimales
        return id.length == 24 && id.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
    }

    private suspend fun fetchAndCacheIncidents() = withContext(ioDispatcher) {
        // tu lógica exacta de ETag y API
        val oldEtag = prefs.data.first()[ETAG_KEY]
        val resp = api.getAllIncidents(eTag = oldEtag)
        if (resp.code() == 200) {
            resp.body()?.let { dtos ->
                dao.clearAll()
                dao.insertAll(dtos.map { it.toEntity() })
            }
            resp.headers()["ETag"]?.let { newEtag ->
                prefs.edit { it[ETAG_KEY] = newEtag }
            }
        }
    }
}