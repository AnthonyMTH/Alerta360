package com.unsa.alerta360.domain.repository

import com.unsa.alerta360.domain.model.Incident
import kotlinx.coroutines.flow.Flow

interface IncidentRepository {
    suspend fun createIncident(incident: Incident): Incident?
    suspend fun getAllIncidents(): List<Incident>
    suspend fun getIncident(id: String): Incident?
    fun observeIncidents(): Flow<List<Incident>>
}