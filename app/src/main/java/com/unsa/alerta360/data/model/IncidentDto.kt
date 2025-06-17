package com.unsa.alerta360.data.model

data class IncidentDto(
    val _id: String? = null,
    val description: String,
    val incidentType: String,
    val ubication: String,
    val geolocation: String,
    val evidence: List<String>,
    val user_id: String,
    val title: String,
    val district: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val __v: Int? = null
)