package com.unsa.alerta360.domain.repository

import com.unsa.alerta360.domain.model.Incident

interface IncidentRepository {
    suspend fun createIncident(incident: Incident): Incident?
    suspend fun getAllIncidents(): List<Incident>
    suspend fun getIncident(id: String): Incident?
}