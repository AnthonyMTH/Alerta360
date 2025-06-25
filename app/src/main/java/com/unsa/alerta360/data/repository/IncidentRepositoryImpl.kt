package com.unsa.alerta360.data.repository

import android.util.Log
import com.unsa.alerta360.data.local.dao.IncidentDao
import com.unsa.alerta360.data.mapper.toDomain
import com.unsa.alerta360.data.mapper.toDto
import com.unsa.alerta360.data.mapper.toEntity
import com.unsa.alerta360.data.network.IncidentApi
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}

class IncidentRepositoryImpl @Inject constructor(
    private val api: IncidentApi,
    private val dao: IncidentDao
) : IncidentRepository {

    override suspend fun getAllIncidents(): List<Incident> {
        val response = api.getAllIncidents()
        if (response.isSuccessful) {
            return response.body()?.map { it.toDomain() } ?: emptyList()
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    override fun observeAllIncidents(): Flow<Resource<List<Incident>>> = networkBoundResource(
        query = {
            dao.getAll().map { entities ->
                Log.d("RoomQuery", "Obtenidos ${entities.size} incidentes de Room")
                entities.map { it.toDomain() }
            }
        },
        fetch = {
            val response = api.getAllIncidents()
            if (response.isSuccessful) {
                val body = response.body() ?: emptyList()
                Log.d("ApiFetch", "API devolvió ${body.size} incidentes")
                body
            } else {
                throw Exception("Error al obtener incidentes: ${response.code()}")
            }
        },
        saveFetchResult = { remoteIncidents ->
            Log.d("RoomSave", "Guardando ${remoteIncidents.size} incidentes en Room")
            dao.clearAll()
            dao.insertAll(remoteIncidents.map { it.toEntity() })
            Log.d("RoomSave", "Incidentes guardados correctamente")
        },
        shouldFetch = { localData ->
            val shouldFetch = localData.isEmpty()
            Log.d("ShouldFetch", "¿Debe hacer fetch?: $shouldFetch")
            shouldFetch
        }
    )

    override suspend fun getIncident(id: String): Incident? {
        return try {
            val response = api.getIncident(id)
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e("IncidentRepository", "Error loading incident $id: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("IncidentRepository", "Exception loading incident $id: ${e.message}", e)
            null
        }
    }

    override suspend fun createIncident(incident: Incident): Incident? {
        return try {
            val response = api.createIncident(incident.toDto())
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e("IncidentRepository", "Error creating incident: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("IncidentRepository", "Exception creating incident: ${e.message}", e)
            null
        }
    }
}

fun <ResultType, RequestType> networkBoundResource(
    query: () -> Flow<ResultType>,
    fetch: suspend () -> RequestType,
    saveFetchResult: suspend (RequestType) -> Unit,
    shouldFetch: (ResultType) -> Boolean = { true }
): Flow<Resource<ResultType>> = flow {
    val data = query().first()
    emit(Resource.Loading(data))
    if (shouldFetch(data)) {
        try {
            val fetched = fetch()
            saveFetchResult(fetched)
            emitAll(query().map { Resource.Success(it) })
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error desconocido", data))
        }
    } else {
        emitAll(query().map { Resource.Success(it) })
    }
}