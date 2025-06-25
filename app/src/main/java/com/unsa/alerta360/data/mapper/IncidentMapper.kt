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

fun IncidentDto.toEntity(): IncidentEntity = IncidentEntity(
    _id = this._id ?: "",
    title = this.title ?: "",
    description = this.description ?: "",
    incidentType = this.incidentType ?: "",
    ubication = this.ubication ?: "",
    geolocation = this.geolocation ?: "",
    district = this.district ?: "",
    evidence = this.evidence ?: emptyList(),
    user_id = this.user_id ?: "",
    createdAt = this.createdAt ?: "",
    updatedAt = this.updatedAt ?: "",
    __v = this.__v ?: 0
)

// Entity → Domain
fun IncidentEntity.toDomain(): Incident = Incident(
    _id = this._id,
    description = this.description,
    incidentType = this.incidentType,
    ubication = this.ubication,
    geolocation = this.geolocation,
    evidence = this.evidence,
    district = this.district,
    user_id = this.user_id,
    title = this.title,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    __v = this.__v
)