package com.unsa.alerta360.data.mapper

import com.unsa.alerta360.data.local.entity.IncidentEntity
import com.unsa.alerta360.data.model.IncidentDto
import com.unsa.alerta360.domain.model.Incident

fun IncidentDto.toDomain(): Incident = Incident(
    _id, description, incidentType, ubication, geolocation,
    evidence, district, user_id, title, createdAt, updatedAt, __v
)

fun Incident.toDto(): IncidentDto = IncidentDto(
    _id, description, incidentType, ubication, geolocation,
    district, evidence, user_id, title, createdAt, updatedAt, __v
)

fun IncidentDto.toEntity(): IncidentEntity =
    IncidentEntity(
        id          = _id!!,
        description = description,
        incidentType= incidentType,
        ubication   = ubication,
        geolocation = geolocation,
        district    = district,
        title       = title,
        userId      = user_id,
        evidence    = evidence,
        createdAt   = createdAt,
        updatedAt   = updatedAt,
        version     = __v ?: 0
)

fun IncidentEntity.toDomain(): com.unsa.alerta360.domain.model.Incident =
    com.unsa.alerta360.domain.model.Incident(
        _id          = id,
        description  = description,
        incidentType = incidentType,
        ubication    = ubication,
        geolocation  = geolocation,
        district     = district,
        title        = title,
        user_id      = userId,
        evidence     = evidence,
        createdAt    = createdAt,
        updatedAt    = updatedAt,
        __v          = version
)