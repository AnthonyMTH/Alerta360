package com.unsa.alerta360.data.mapper

import com.unsa.alerta360.data.model.IncidentDto
import com.unsa.alerta360.domain.model.Incident

fun IncidentDto.toDomain(): Incident = Incident(
    _id, description, incidentType, ubication, geolocation,
    evidence, user_id, title, createdAt, updatedAt, __v
)

fun Incident.toDto(): IncidentDto = IncidentDto(
    _id, description, incidentType, ubication, geolocation,
    evidence, user_id, title, createdAt, updatedAt, __v
)