package com.unsa.alerta360.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey val _id: String,
    val description: String,
    val incidentType: String,
    val ubication: String,
    val geolocation: String,
    val district: String,
    val evidence: List<String>,
    val user_id: String,
    val title: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)