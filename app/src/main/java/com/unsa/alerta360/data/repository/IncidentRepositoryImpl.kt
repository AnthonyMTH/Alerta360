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
        // 1) Siempre guardo local
        val localEntity = domain.toEntity().copy(
            id       = domain._id ?: UUID.randomUUID().toString(),
            synced   = false,
            createdAt= System.now().toString(),
            updatedAt= System.now().toString()
        )
        dao.insert(localEntity)

        // 2) Si hay red, intento remitir remoto
        if (NetworkMonitor.hasNetwork(context)) {
            try {
                val resp = api.createIncident(localEntity.toDto())
                if (resp.isSuccessful) {
                    val remoteDto = resp.body()!!
                    dao.insert(remoteDto.toEntity().copy(synced = true))
                    return remoteDto.toDomain()
                }
            } catch (_: Exception) { /* fallo, lo dejamos local */ }
        }

        // 3) Devuelvo lo local (pendiente)
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