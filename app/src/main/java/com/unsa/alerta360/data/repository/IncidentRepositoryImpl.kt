package com.unsa.alerta360.data.repository

import android.util.Log
import com.unsa.alerta360.data.mapper.toDomain
import com.unsa.alerta360.data.mapper.toDto
import com.unsa.alerta360.data.network.IncidentApi
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import javax.inject.Inject

class IncidentRepositoryImpl @Inject constructor(
        private val api: IncidentApi
) : IncidentRepository {

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

    override suspend fun getAllIncidents(): List<Incident> {
        return try {
            val response = api.getAllIncidents()
            if (response.isSuccessful) {
                val incidents = response.body()?.map { it.toDomain() } ?: emptyList()
                Log.d("IncidentRepository", "Successfully loaded ${incidents.size} incidents")
                incidents
            } else {
                Log.e("IncidentRepository", "Error loading incidents: ${response.code()} - ${response.message()}")
                Log.e("IncidentRepository", "Response body: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("IncidentRepository", "Exception loading incidents: ${e.message}", e)
            throw e // Re-lanzar para que el ViewModel pueda capturar el error específico
        }
    }
    override suspend fun getIncident(id: String): Incident? {
        return try {
            val response = api.getIncident(id)
            if (response.isSuccessful) {
                val incident = response.body()?.toDomain()
                Log.d("IncidentRepository", "Incident loaded successfully: $incident")
                incident
            } else {
                Log.e("IncidentRepository", "Error loading incident $id: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("IncidentRepository", "Exception loading incident $id: ${e.message}", e)
            null
        }
    }
}