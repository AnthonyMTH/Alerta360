package com.unsa.alerta360.data.mapper

import com.unsa.alerta360.data.model.IncidentDto
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.data.local.entity.IncidentEntity
import com.unsa.alerta360.data.local.entity.SyncStatus
import java.util.UUID

fun IncidentDto.toDomain(): Incident = Incident(
    _id, description, incidentType, ubication, geolocation,
    evidence, district, user_id, title, createdAt, updatedAt, __v
)

fun Incident.toDto(): IncidentDto = IncidentDto(
    _id, description, incidentType, ubication, geolocation,
    district, evidence, user_id, title, createdAt, updatedAt, __v
)

// Conversiones para Entity (local database)
fun Incident.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING_SYNC): IncidentEntity {
    val currentTime = System.currentTimeMillis()
    return IncidentEntity(
        localId = _id ?: UUID.randomUUID().toString(),
        serverId = if (syncStatus == SyncStatus.SYNCED) _id else null,
        title = title,
        description = description,
        incidentType = incidentType,
        ubication = ubication,
        geolocation = geolocation,
        evidence = evidence,
        district = district,
        userId = user_id,
        syncStatus = syncStatus,
        createdAt = currentTime,
        updatedAt = currentTime
    )
}

fun IncidentEntity.toDomain(): Incident = Incident(
    _id = serverId ?: localId,
    description = description,
    incidentType = incidentType,
    ubication = ubication,
    geolocation = geolocation,
    evidence = evidence,
    district = district,
    user_id = userId,
    title = title,
    createdAt = null, // Room maneja timestamps como Long
    updatedAt = null,
    __v = null
)

fun IncidentEntity.toDto(): IncidentDto = IncidentDto(
    _id = serverId,
    description = description,
    incidentType = incidentType,
    ubication = ubication,
    geolocation = geolocation,
    district = district,
    evidence = evidence,
    user_id = userId,
    title = title
)