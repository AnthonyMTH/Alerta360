package com.unsa.alerta360.data.mapper

import com.unsa.alerta360.data.local.entity.IncidentEntity
import com.unsa.alerta360.data.model.IncidentDto
import com.unsa.alerta360.domain.model.Incident
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

fun IncidentDto.toDomain(): Incident = Incident(
    _id, description, incidentType, ubication, geolocation,
    evidence, district, user_id, title, createdAt, updatedAt, __v
)

fun Incident.toDto(): IncidentDto = IncidentDto(
    _id, description, incidentType, ubication, geolocation,
    district, evidence, user_id, title, createdAt, updatedAt, __v
)

fun IncidentDto.toEntity(): IncidentEntity = IncidentEntity(
    id          = this._id ?: UUID.randomUUID().toString(),
    title       = this.title,
    description = this.description,
    incidentType= this.incidentType,
    ubication   = this.ubication,
    geolocation = this.geolocation,
    evidence    = this.evidence,
    district    = this.district,
    userId      = this.user_id,
    createdAt   = this.createdAt?.let { Instant.parse(it) }.toString(),
    updatedAt   = this.updatedAt?.let { Instant.parse(it) }.toString(),
    version     = this.__v,
    synced      = false
)

fun Incident.toEntity(): IncidentEntity = IncidentEntity(
    id = this._id ?: UUID.randomUUID().toString(),
    title = this.title,
    description = this.description,
    incidentType = this.incidentType,
    ubication = this.ubication,
    geolocation = this.geolocation,
    evidence = this.evidence,
    district = this.district,
    userId = this.user_id,
    createdAt = (this.createdAt?.let { Instant.parse(it) } ?: Clock.System.now().toString()).toString(),
    updatedAt = (this.updatedAt?.let { Instant.parse(it) } ?: Clock.System.now().toString()).toString(),
    version = this.__v,
    synced = false
)

/** Entidad → DTO */
fun IncidentEntity.toDto(): IncidentDto = IncidentDto(
    _id         = this.id,
    description = this.description,
    incidentType= this.incidentType,
    ubication   = this.ubication,
    geolocation = this.geolocation,
    district    = this.district,
    evidence    = this.evidence,
    user_id     = this.userId,
    title       = this.title,
    createdAt   = this.createdAt?.toString(),
    updatedAt   = this.updatedAt?.toString(),
    __v         = this.version
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