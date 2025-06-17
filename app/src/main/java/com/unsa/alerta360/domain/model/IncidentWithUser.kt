package com.unsa.alerta360.domain.model

data class IncidentWithUser(
    val incident: Incident,
    val userName: String = ""
)