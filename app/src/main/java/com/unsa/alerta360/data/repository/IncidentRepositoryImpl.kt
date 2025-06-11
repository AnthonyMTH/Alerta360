package com.unsa.alerta360.data.repository

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
        val response = api.createIncident(incident.toDto())
        return if (response.isSuccessful) response.body()?.toDomain() else null
    }

    override suspend fun getAllIncidents(): List<Incident> {
        val response = api.getAllIncidents()
        return if (response.isSuccessful) response.body()?.map { it.toDomain() } ?: emptyList()
        else emptyList()
    }
}