package com.unsa.alerta360.data.repository

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
import com.unsa.alerta360.di.IoDispatcher
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IncidentRepositoryImpl @Inject constructor(
    private val api: IncidentApi,
    private val dao: IncidentDao,
    private val prefs: DataStore<Preferences>,      // para el ETag
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher // define tu dispatcher
) : IncidentRepository {


    private val ETAG_KEY = stringPreferencesKey("ETAG_INCIDENTS")

    override suspend fun createIncident(incident: Incident): Incident? = withContext(ioDispatcher) {
        try {
            val resp = api.createIncident(incident.toDto())
            if (resp.isSuccessful) {
                resp.body()?.let { dto ->
                    val domain = dto.toDomain()
                    //  Añadimos al caché local
                    dao.insertAll(listOf(dto.toEntity()))
                    return@withContext domain
                }
            }
            Log.e("IncidentRepo", "Create failed: ${resp.code()} ${resp.message()}")
            null
        } catch (e: Exception) {
            Log.e("IncidentRepo", "Exception create: ${e.message}", e)
            null
        }
    }

    override fun observeIncidents(): Flow<List<Incident>> =
        dao.observeAll()
            // 1️⃣ Antes de emitir, intento sincronizar pero envuelvo en try/catch
            .onStart {
                try {
                    fetchAndCacheIncidents()
                } catch (e: Exception) {
                    Log.e("IncidentRepo", "No pude sincronizar con remoto, usar local", e)
                }
            }
            // 2️⃣ Transformo la entidad de Room a mi modelo de dominio
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getAllIncidents(): List<Incident> = withContext(ioDispatcher) {
    // Para llamadas one-shot también refrescamos antes de leer
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


    // 2️⃣ Lógica compartida de “remote-first”: ETag, API, Room y DataStore
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
        // si hay 304 o error de negocio, NO TOCO la base
    }
}